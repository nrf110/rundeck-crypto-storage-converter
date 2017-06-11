package com.github.nrf110.rundeck.core;

import org.junit.Assert;
import org.junit.Test;

public class MarshallerTest {
    @Test
    public void testMarshall() throws Exception {
        CryptoCodec original = CryptoCodec.create();
        String marshalled = Marshaller.marshall(original);
        CryptoCodec unmarshalled = Marshaller.unmarshall(marshalled);

        Assert.assertEquals(original, unmarshalled);
    }
}
