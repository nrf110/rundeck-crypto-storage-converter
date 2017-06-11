package com.github.nrf110.rundeck.core;

import com.github.nrf110.rundeck.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class CryptoCodecTest {
    private static final String VALUE = "This is some text and symbols $#!@$)(";
    private static final String PASSWORD = "abcdefghijklmnop";

    @Test
    public void testEncrypt() throws Exception {
        byte[] inputBytes = VALUE.getBytes("UTF-8");

        CryptoCodec codec = CryptoCodec.create();
        byte[] result = TestUtils.encrypt(codec, inputBytes, PASSWORD);

        Assert.assertTrue("result must be non-empty", result.length > 0);
        Assert.assertTrue("result should be different than the input value", !Arrays.equals(inputBytes, result));
    }

    @Test
    public void testDecrypt() throws Exception {
        byte[] inputBytes = VALUE.getBytes("UTF-8");

        CryptoCodec codec = CryptoCodec.create();
        byte[] encrypted = TestUtils.encrypt(codec, inputBytes, PASSWORD);
        byte[] result = decrypt(codec, encrypted);

        Assert.assertArrayEquals(inputBytes, result);
    }

    @Test
    public void testDecryptOnDeserializedInstance() throws Exception {
        byte[] inputBytes = VALUE.getBytes("UTF-8");

        CryptoCodec encryptCodec = CryptoCodec.create();
        byte[] encrypted = TestUtils.encrypt(encryptCodec, inputBytes, PASSWORD);

        CryptoCodec decodeCodec = Marshaller.unmarshall(Marshaller.marshall(encryptCodec));
        byte[] result = decrypt(decodeCodec, encrypted);

        Assert.assertArrayEquals(inputBytes, result);
    }

    @Test
    public void testEqualsShouldBeTrue() {
        Random random = new Random();
        byte[] salt = new byte[8];
        byte[] iv = new byte[32];
        random.nextBytes(salt);
        random.nextBytes(iv);

        CryptoCodec a = new CryptoCodec(salt, iv);
        CryptoCodec b = new CryptoCodec(salt, iv);

        Assert.assertEquals(a, b);
    }

    @Test
    public void testEqualsShouldBeFalse() {
        Random random = new Random();

        byte[] saltA = new byte[8];
        byte[] ivA = new byte[32];
        random.nextBytes(saltA);
        random.nextBytes(ivA);
        CryptoCodec a = new CryptoCodec(saltA, ivA);

        byte[] saltB = new byte[8];
        byte[] ivB = new byte[32];
        random.nextBytes(saltB);
        random.nextBytes(ivB);
        CryptoCodec b = new CryptoCodec(saltB, ivB);

        Assert.assertNotEquals(a, b);
    }

    private byte[] decrypt(CryptoCodec codec, byte[] input) throws Exception {
        InputStream is = codec.decrypt(new ByteArrayInputStream(input), PASSWORD);
        return TestUtils.readInputStream(is);
    }
}
