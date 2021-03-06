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

import org.cloudfoundry.dropsonde.events.EventFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.bus.firehose.config.BusConfig;
import org.springframework.bus.firehose.config.FirehoseProperties;
import org.springframework.bus.firehose.support.ProtocolGenerator;
import org.springframework.integration.support.MessageBuilder;

/**
 * @author Vinicius Carvalho
 */
public class FilterTests {

    @Test
    public void filterTest() throws Exception {
        BusConfig config = new BusConfig();
        FirehoseProperties properties = new FirehoseProperties();
        properties.setDopplerEvents("HttpStartStop,LogMessage");

        EventFactory.Envelope startStop = ProtocolGenerator.httpStartStopEvent();
        EventFactory.Envelope logMessage = ProtocolGenerator.logMessage();



        config.setFirehoseProperties(properties);

        boolean acceptHttp = config.filterMessages(MessageBuilder.withPayload(startStop).setHeader("EventType",startStop.getEventType().name()).build());
        boolean acceptLog = config.filterMessages(MessageBuilder.withPayload(logMessage).setHeader("EventType",logMessage.getEventType().name()).build());
        properties.setDopplerEvents("ValueMetric");
        boolean failHttp = config.filterMessages(MessageBuilder.withPayload(startStop).setHeader("EventType",startStop.getEventType().name()).build());

        Assert.assertTrue(acceptHttp);
        Assert.assertTrue(acceptLog);
        Assert.assertFalse(failHttp);



    }

}
