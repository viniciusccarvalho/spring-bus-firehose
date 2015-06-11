package org.springframework.bus.firehose.config;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.cloudfoundry.dropsonde.events.EventFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
import org.springframework.messaging.MessageChannel;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by vcarvalho on 6/11/15.
 */
@Configuration
public class BusConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Environment env;

    @Autowired
    private FirehoseProperties firehoseProperties;

    @Bean
    public WebSocketClient webSocketClient(){
        return new StandardWebSocketClient();

                //new JettyWebSocketClient(new org.eclipse.jetty.websocket.client.WebSocketClient(new SslContextFactory(true)));
    }

    @Bean
    public ClientWebSocketContainer webSocketContainer(WebSocketClient client) throws Exception{
        ClientWebSocketContainer container = new ClientWebSocketContainer(client,getDopplerEndpoint());
        HttpHeaders headers = new HttpHeaders();


        if(!StringUtils.isEmpty(firehoseProperties.getUsername())){
            OauthClient oauthClient = new OauthClient(new URL(firehoseProperties.getAuthenticationUrl()),new RestUtil().createRestTemplate(null,true));
            oauthClient.init(new CloudCredentials(firehoseProperties.getUsername(), firehoseProperties.getPassword()));
            headers.add("Authorization", "bearer " + oauthClient.getToken().getValue());
        }else{
            headers.add("Authorization","");
        }
        container.setHeaders(headers);

        container.setOrigin(env.getProperty("firehose.ws.origin", "http://localhost"));
        return container;
    }

    @Bean
    public WebSocketInboundChannelAdapter webSocketInboundChannelAdapter(ClientWebSocketContainer webSocketContainer, ByteBufferMessageConverter converter, MessageChannel byteChannel){
        WebSocketInboundChannelAdapter adapter = new WebSocketInboundChannelAdapter(webSocketContainer);
        adapter.setMessageConverters(Collections.singletonList(converter));
        adapter.setOutputChannel(byteChannel);
        adapter.setPayloadType(ByteBuffer.class);
        return adapter;
    }

    @Bean
    public MessageChannel byteChannel(){
        return new DirectChannel();
    }


    @Filter(outputChannel = "output", inputChannel = "envelopeChannel")
    public boolean filterMessages(EventFactory.Envelope envelope){
        if(StringUtils.isEmpty(firehoseProperties.getDopplerEvents())){
            return true;
        }
        else{
            for(String event : firehoseProperties.getDopplerEvents().split(",")){
                if (event.equalsIgnoreCase(envelope.getEventType().toString()))
                    return true;
            }
        }
        return false;
    }

    @Bean
    public MessageChannel output(){
        return new DirectChannel();
    }


    private String getDopplerEndpoint(){

        String url =  StringUtils.isEmpty(firehoseProperties.getDopplerUrl()) ? "wss://doppler."+firehoseProperties.getCfDomain() : firehoseProperties.getDopplerUrl();
        String subscription =  StringUtils.isEmpty(firehoseProperties.getDopplerSubscription()) ?  "firehose-x" : firehoseProperties.getDopplerSubscription();
        return url+"/firehose/"+subscription;
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
