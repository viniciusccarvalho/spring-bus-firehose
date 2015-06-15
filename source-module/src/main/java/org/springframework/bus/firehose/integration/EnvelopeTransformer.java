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

import com.googlecode.protobuf.format.JsonFormat;
import org.cloudfoundry.dropsonde.events.EventFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bus.firehose.config.FirehoseProperties;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author Vinicius Carvalho
 */
@Component
public class EnvelopeTransformer {


    private FirehoseProperties properties;

    @Autowired
    public EnvelopeTransformer(FirehoseProperties properties){
        this.properties = properties;
    }

    @Transformer(inputChannel = "byteChannel", outputChannel = "envelopeChannel")
    public Message<?> readEnvelope(Message<?> message) throws Exception {
        byte[] bytes = (byte[]) message.getPayload();
        EventFactory.Envelope envelope = EventFactory.Envelope.parseFrom(bytes);
        Object payload = null;
        payload = (properties.getOutputJson()) ? JsonFormat.printToString(envelope) : envelope;
        Message<?> result = MessageBuilder.withPayload(payload).setHeader("EventType",envelope.getEventType().name()).build();
        return result;
    }
}
