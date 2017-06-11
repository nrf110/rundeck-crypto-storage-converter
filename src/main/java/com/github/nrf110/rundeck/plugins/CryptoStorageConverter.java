package com.github.nrf110.rundeck.plugins;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.storage.ResourceMetaBuilder;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.storage.StorageConverterPlugin;
import com.github.nrf110.rundeck.core.CryptoCodec;
import com.github.nrf110.rundeck.core.CryptoException;
import com.github.nrf110.rundeck.core.CryptoStream;
import com.github.nrf110.rundeck.core.Marshaller;
import org.rundeck.storage.api.HasInputStream;
import org.rundeck.storage.api.Path;

import java.io.IOException;

@Plugin(service = ServiceNameConstants.StorageConverter, name = CryptoStorageConverter.PROVIDER_NAME)
public class CryptoStorageConverter implements StorageConverterPlugin {

    @SuppressWarnings("unused")
    public CryptoStorageConverter() {}

    CryptoStorageConverter(String password) {
        this.password = password;
    }

    static final String PROVIDER_NAME = "encryption-storage-converter";

    @PluginProperty(title = "Password", description = "The secret key/password used in the encryption algorithm.", required = true)
    private String password;

    @Override
    public HasInputStream readResource(Path path, ResourceMetaBuilder resourceMetaBuilder, HasInputStream hasInputStream) {
        String data = resourceMetaBuilder.getResourceMeta().get(PROVIDER_NAME);
        if (data != null) {
            try {
                CryptoCodec codec = Marshaller.unmarshall(data);
                return new CryptoStream(hasInputStream, codec, password, false);
            } catch (IOException|ClassNotFoundException e) {
                throw new CryptoException(e);
            }
        }

        return null;
    }

    @Override
    public HasInputStream createResource(Path path, ResourceMetaBuilder resourceMetaBuilder, HasInputStream hasInputStream) {
        return encrypt(hasInputStream, resourceMetaBuilder);
    }

    @Override
    public HasInputStream updateResource(Path path, ResourceMetaBuilder resourceMetaBuilder, HasInputStream hasInputStream) {
        return encrypt(hasInputStream, resourceMetaBuilder);
    }

    private HasInputStream encrypt(HasInputStream wrapped, ResourceMetaBuilder resourceMetaBuilder) {
        CryptoCodec codec = CryptoCodec.create();

        try {
            String marshalled = Marshaller.marshall(codec);
            resourceMetaBuilder.setMeta(PROVIDER_NAME, marshalled);
        } catch (IOException e) {
            throw new CryptoException(e);
        }

        return new CryptoStream(wrapped, codec, password, true);
    }
}
