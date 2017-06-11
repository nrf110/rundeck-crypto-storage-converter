package com.github.nrf110.rundeck.core;

import org.rundeck.storage.api.HasInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CryptoStream implements HasInputStream {
    private HasInputStream wrapped;
    private Boolean encrypt;
    private CryptoCodec codec;
    private String password;

    public CryptoStream(HasInputStream wrapped, CryptoCodec codec, String password, Boolean encrypt) {
        this.wrapped = wrapped;
        this.codec = codec;
        this.encrypt = encrypt;
        this.password = password;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return codec.decrypt(wrapped.getInputStream(), password);
    }

    @Override
    public long writeContent(OutputStream outputStream) throws IOException {
        if (this.encrypt) {
            OutputStream os = codec.encrypt(outputStream, password);
            try {
                return wrapped.writeContent(os);
            } finally {
                os.flush();
                os.close();
                outputStream.flush();
                outputStream.close();
            }
        }

        return 0L;
    }
}
