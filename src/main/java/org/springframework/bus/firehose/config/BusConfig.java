/*
 * Copyright 2015 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.bus.firehose.config;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bus.firehose.integration.ByteBufferMessageConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * @author Vinicius Carvalho
 */
@Configuration
public class BusConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Environment env;

    @Autowired
    private FirehoseProperties firehoseProperties;

    @Bean
    public WebSocketClient webSocketClient() {
//        return new StandardWebSocketClient();
        //@TODO why Jetty client is not working with spring integration 4.2.0? AbstractMethodException being thrown
        return new JettyWebSocketClient(new org.eclipse.jetty.websocket.client.WebSocketClient(new SslContextFactory(true)));
    }

    @Bean
    public ClientWebSocketContainer webSocketContainer(WebSocketClient client) throws Exception {
        ClientWebSocketContainer container = new ClientWebSocketContainer(client, getDopplerEndpoint());
        HttpHeaders headers = new HttpHeaders();


        if (!StringUtils.isEmpty(firehoseProperties.getUsername())) {
            OauthClient oauthClient = new OauthClient(new URL(firehoseProperties.getAuthenticationUrl()), new RestUtil().createRestTemplate(null, true));
            oauthClient.init(new CloudCredentials(firehoseProperties.getUsername(), firehoseProperties.getPassword()));
            headers.add("Authorization", "bearer " + oauthClient.getToken().getValue());
        } else {
            headers.add("Authorization", "");
        }
        container.setHeaders(headers);

        container.setOrigin(env.getProperty("firehose.ws.origin", "http://localhost"));
        return container;
    }

    @Bean
    public WebSocketInboundChannelAdapter webSocketInboundChannelAdapter(ClientWebSocketContainer webSocketContainer, ByteBufferMessageConverter converter, MessageChannel byteChannel) {
        WebSocketInboundChannelAdapter adapter = new WebSocketInboundChannelAdapter(webSocketContainer);
        adapter.setMessageConverters(Collections.singletonList(converter));
        adapter.setOutputChannel(byteChannel);
        adapter.setPayloadType(ByteBuffer.class);
        return adapter;
    }

    @Bean
    public MessageChannel byteChannel() {
        return new DirectChannel();
    }


    @Filter(outputChannel = "output", inputChannel = "envelopeChannel")
    public boolean filterMessages(Message<?> message) {
        if (StringUtils.isEmpty(firehoseProperties.getDopplerEvents())) {
            return true;
        } else {
            for (String event : firehoseProperties.getDopplerEvents().split(",")) {
                if (event.equalsIgnoreCase(message.getHeaders().get("EventType").toString()))
                    return true;
            }
        }
        return false;
    }

    @Bean
    public MessageChannel output() {
        return new DirectChannel();
    }


    private String getDopplerEndpoint() {

        String url = StringUtils.isEmpty(firehoseProperties.getDopplerUrl()) ? "wss://doppler." + firehoseProperties.getCfDomain() : firehoseProperties.getDopplerUrl();
        String subscription = StringUtils.isEmpty(firehoseProperties.getDopplerSubscription()) ? "firehose-x" : firehoseProperties.getDopplerSubscription();
        return url + "/firehose/" + subscription;
    }


    public void setFirehoseProperties(FirehoseProperties firehoseProperties) {
        this.firehoseProperties = firehoseProperties;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext ctx = contextRefreshedEvent.getApplicationContext();
        ctx.getBean(ClientWebSocketContainer.class).start();
        ctx.getBean(WebSocketInboundChannelAdapter.class).start();

    }
}
