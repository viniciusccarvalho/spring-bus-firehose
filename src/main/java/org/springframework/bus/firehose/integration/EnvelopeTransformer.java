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
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

/**
 * @author Vinicius Carvalho
 */
@Component
public class EnvelopeTransformer {

    @Transformer(inputChannel = "byteChannel", outputChannel = "envelopeChannel")
    public Object readEnvelope(GenericMessage<?> message) throws Exception {
        byte[] bytes = (byte[]) message.getPayload();
        EventFactory.Envelope envelope = EventFactory.Envelope.parseFrom(bytes);
        return envelope;
    }
}
