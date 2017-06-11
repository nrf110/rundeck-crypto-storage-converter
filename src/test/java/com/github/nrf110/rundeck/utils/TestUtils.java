package com.github.nrf110.rundeck.utils;

import com.github.nrf110.rundeck.core.CryptoCodec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestUtils {
    public static byte[] readInputStream(InputStream is) throws IOException {
        int length;
        byte[] buffer = new byte[32];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while ((length = is.read(buffer)) != -1) {
            os.write(buffer, 0, length);
        }
        os.flush();
        os.close();
        is.close();
        return os.toByteArray();
    }

    public static byte[] encrypt(CryptoCodec codec, byte[] value, String password) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        OutputStream os = codec.encrypt(buffer, password);
        os.write(value);
        os.flush();
        os.close();
        return buffer.toByteArray();
    }
}
