package com.github.nrf110.rundeck.plugins;

import com.dtolabs.rundeck.core.storage.ResourceMetaBuilder;
import com.github.nrf110.rundeck.core.CryptoCodec;
import com.github.nrf110.rundeck.core.Marshaller;
import com.github.nrf110.rundeck.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.storage.api.HasInputStream;

import java.io.*;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class CryptoStorageConverterTest {
    private static final String PASSWORD = "abcdefghijklmnop";
    private static final String VALUE = "This is some text and symbols $#!@$)(";

    @Test
    public void testReadResourceShouldDecryptData() throws Exception {
        CryptoCodec codec = CryptoCodec.create();
        String serialized = Marshaller.marshall(codec);
        CryptoStorageConverter subject = new CryptoStorageConverter(PASSWORD);
        HasInputStream stub = readStub(TestUtils.encrypt(codec, VALUE.getBytes("UTF-8"), PASSWORD));

        HashMap meta = new HashMap();
        meta.put(CryptoStorageConverter.PROVIDER_NAME, serialized);
        HasInputStream output = subject.readResource(null, new ResourceMetaBuilder(meta), stub);
        byte[] result = TestUtils.readInputStream(output.getInputStream());
        Assert.assertArrayEquals(VALUE.getBytes(), result);
    }

    @Test
    public void testReadResourceShouldReturnNull() throws Exception {
        CryptoStorageConverter subject = new CryptoStorageConverter(PASSWORD);
        HasInputStream stub = readStub(VALUE.getBytes("UTF-8"));

        HasInputStream result = subject.readResource(null, new ResourceMetaBuilder(), stub);
        Assert.assertNull(result);
    }

    @Test
    public void testCreateResource() throws Exception {
        CryptoStorageConverter subject = new CryptoStorageConverter(PASSWORD);
        ResourceMetaBuilder metaBuilder = new ResourceMetaBuilder();
        HasInputStream his = subject.createResource(null, metaBuilder, writeStub(VALUE.getBytes("UTF-8")));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        his.writeContent(os);
        os.flush();
        os.close();

        byte[] result = os.toByteArray();
        String serializedCodec = metaBuilder.getResourceMeta().get(CryptoStorageConverter.PROVIDER_NAME);
        Assert.assertNotNull(serializedCodec);

        CryptoCodec codec = Marshaller.unmarshall(serializedCodec);
        byte[] encrypted = TestUtils.encrypt(codec, VALUE.getBytes("UTF-8"), PASSWORD);
        Assert.assertArrayEquals(encrypted, result);
    }

    @Test
    public void testUpdateResource() throws Exception {
        CryptoStorageConverter subject = new CryptoStorageConverter(PASSWORD);
        ResourceMetaBuilder metaBuilder = new ResourceMetaBuilder();
        HasInputStream his = subject.updateResource(null, metaBuilder, writeStub(VALUE.getBytes("UTF-8")));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        his.writeContent(os);
        os.flush();
        os.close();

        byte[] result = os.toByteArray();
        String serializedCodec = metaBuilder.getResourceMeta().get(CryptoStorageConverter.PROVIDER_NAME);
        Assert.assertNotNull(serializedCodec);

        CryptoCodec codec = Marshaller.unmarshall(serializedCodec);
        byte[] encrypted = TestUtils.encrypt(codec, VALUE.getBytes("UTF-8"), PASSWORD);
        Assert.assertArrayEquals(encrypted, result);
    }

    private HasInputStream readStub(byte[] value) {
        return new HasInputStream() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(value);
            }

            @Override
            public long writeContent(OutputStream outputStream) throws IOException {
                return 0;
            }
        };
    }

    private HasInputStream writeStub(byte[] value) {
        return new HasInputStream() {
            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public long writeContent(OutputStream outputStream) throws IOException {
                outputStream.write(value);
                return value.length;
            }
        };
    }
}
