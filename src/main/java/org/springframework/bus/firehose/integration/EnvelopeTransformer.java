package org.springframework.bus.firehose.integration;

import org.cloudfoundry.dropsonde.events.EventFactory;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

/**
 * Created by vcarvalho on 6/10/15.
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
