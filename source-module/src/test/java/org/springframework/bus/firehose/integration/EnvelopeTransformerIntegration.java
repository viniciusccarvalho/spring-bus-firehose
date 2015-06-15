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

import junit.framework.Assert;
import org.cloudfoundry.dropsonde.events.EventFactory;
import org.junit.Test;
import org.springframework.bus.firehose.config.FirehoseProperties;
import org.springframework.bus.firehose.support.ProtocolGenerator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

/**
 * @author Vinicius Carvalho
 */
public class EnvelopeTransformerIntegration {


    @Test
    public void outputPojo() throws Exception {
        FirehoseProperties properties = new FirehoseProperties();

        EnvelopeTransformer transformer = new EnvelopeTransformer(properties);

        EventFactory.Envelope payload =  ProtocolGenerator.httpStartStopEvent();

        Message<?> message = MessageBuilder.withPayload(payload.toByteArray()).build();

        Message<?> transformedMessage = transformer.readEnvelope(message);

        Assert.assertTrue((transformedMessage.getPayload() instanceof EventFactory.Envelope));

    }

    @Test
    public void outputJson() throws Exception {
        FirehoseProperties properties = new FirehoseProperties();
        properties.setOutputJson(true);
        EnvelopeTransformer transformer = new EnvelopeTransformer(properties);

        EventFactory.Envelope payload =  ProtocolGenerator.httpStartStopEvent();

        Message<?> message = MessageBuilder.withPayload(payload.toByteArray()).build();

        Message<?> transformedMessage = transformer.readEnvelope(message);

        Assert.assertTrue((transformedMessage.getPayload() instanceof String));
    }

}
