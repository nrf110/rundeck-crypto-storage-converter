package com.github.nrf110.rundeck.core;

import java.io.*;
import java.util.Base64;

public class Marshaller {
    public static String marshall(CryptoCodec codec) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream serializer = null;
        String result;

        try {
            serializer = new ObjectOutputStream(buffer);
            serializer.writeObject(codec);
            serializer.flush();
            buffer.flush();

            byte[] data = buffer.toByteArray();
            result = Base64.getEncoder().encodeToString(data);
        } finally {
            if (serializer != null) {
                serializer.close();
            }

            buffer.close();
        }

        return result;
    }

    public static CryptoCodec unmarshall(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream is = null;
        ObjectInputStream deserializer = null;
        CryptoCodec result;

        try {
            is = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            deserializer = new ObjectInputStream(is);
            result = (CryptoCodec) deserializer.readObject();
        } finally {
            if (deserializer != null) {
                deserializer.close();
            }

            if (is != null) {
                is.close();
            }
        }

        return result;
    }
}
