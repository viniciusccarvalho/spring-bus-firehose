package org.springframework.bus.firehose;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.bus.runner.EnableMessageBus;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;

import java.util.UUID;

/**
 * Created by vcarvalho on 6/10/15.
 */

@SpringBootApplication
@EnableMessageBus
public class Application {


    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }






}
