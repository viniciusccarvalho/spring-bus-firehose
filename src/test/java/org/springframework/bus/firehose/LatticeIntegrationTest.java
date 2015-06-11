package org.springframework.bus.firehose;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;

/**
 * Created by vcarvalho on 6/11/15.
 *
 * This is an integrationTest it won't run without a local lattice and a rabbitmq broker started
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class LatticeIntegrationTest {

    @Test
    public void start() throws Exception{
       Thread.sleep(60000L);
    }

}
