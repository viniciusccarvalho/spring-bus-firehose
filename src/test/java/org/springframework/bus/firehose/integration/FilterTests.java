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

package org.springframework.bus.firehose.integration;

import com.google.protobuf.ByteString;
import junit.framework.Assert;
import org.cloudfoundry.dropsonde.events.EventFactory;
import org.cloudfoundry.dropsonde.events.HttpFactory;
import org.cloudfoundry.dropsonde.events.LogFactory;
import org.cloudfoundry.dropsonde.events.UuidFactory;
import org.junit.Test;
import org.springframework.bus.firehose.config.BusConfig;
import org.springframework.bus.firehose.config.FirehoseProperties;

/**
 * @author Vinicius Carvalho
 */
public class FilterTests {

    @Test
    public void filterTest() throws Exception {
        BusConfig config = new BusConfig();
        FirehoseProperties properties = new FirehoseProperties();
        properties.setDopplerEvents("HttpStartStop,LogMessage");

        EventFactory.Envelope startStop = EventFactory.Envelope.newBuilder().setOrigin("localhost").setTimestamp(System.currentTimeMillis())

                .setEventType(EventFactory.Envelope.EventType.HttpStartStop)
                .setHttpStartStop(HttpFactory.HttpStartStop.newBuilder().setContentLength(200L)
                        .setStartTimestamp(System.currentTimeMillis())
                        .setStopTimestamp(System.currentTimeMillis() + 100)
                        .setPeerType(HttpFactory.PeerType.Server)
                        .setStatusCode(200)
                        .setRemoteAddress("localhost")
                        .setUserAgent("Gecko")
                        .setRequestId(UuidFactory.UUID.newBuilder().setHigh(1).setLow(1).build())
                        .setMethod(HttpFactory.Method.GET)
                        .setUri("http://acme.com/info")
                        .build())
                .build();


        EventFactory.Envelope logMessage = EventFactory.Envelope.newBuilder().setOrigin("localhost").setTimestamp(System.currentTimeMillis())
                .setEventType(EventFactory.Envelope.EventType.LogMessage)
                .setLogMessage(LogFactory.LogMessage.newBuilder()
                                .setTimestamp(System.currentTimeMillis())
                                .setAppId("app")
                                .setMessage(ByteString.copyFrom("foo".getBytes()))
                                .setMessageType(LogFactory.LogMessage.MessageType.OUT)
                                .build()
                ).build();


        config.setFirehoseProperties(properties);

        boolean acceptHttp = config.filterMessages(startStop);
        boolean acceptLog = config.filterMessages(logMessage);
        properties.setDopplerEvents("ValueMetric");
        boolean failHttp = config.filterMessages(startStop);

        Assert.assertTrue(acceptHttp);
        Assert.assertTrue(acceptLog);
        Assert.assertFalse(failHttp);



    }

}
