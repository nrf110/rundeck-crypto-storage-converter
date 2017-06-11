package com.github.nrf110.rundeck.core;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class CryptoCodec implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private byte[] salt;
    private byte[] iv;

    public static CryptoCodec create() {
        SecureRandom rand = new SecureRandom();

        byte[] salt = new byte[8];
        rand.nextBytes(salt);

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            int blockSize = cipher.getBlockSize();
            byte[] iv = new byte[blockSize];
            rand.nextBytes(iv);

            return new CryptoCodec(salt, iv);
        } catch (GeneralSecurityException e) {
            throw new CryptoException(e);
        }
    }

    @SuppressWarnings("unused")
    public CryptoCodec() {

    }

    CryptoCodec(byte[] salt, byte[] iv) {
        this.salt = salt;
        this.iv = iv;
    }

    public InputStream decrypt(InputStream source, String password) {
        return new CipherInputStream(source, getCipher(Cipher.DECRYPT_MODE, password));
    }

    public OutputStream encrypt(OutputStream dest, String password) {
        return new CipherOutputStream(dest, getCipher(Cipher.ENCRYPT_MODE, password));
    }

    private Cipher getCipher(int mode, String password) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, getKey(password), new IvParameterSpec(iv));
            return cipher;
        } catch (GeneralSecurityException e) {
            throw new CryptoException(e);
        }
    }

    private SecretKey getKey(String password) {
        try {
            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (GeneralSecurityException e) {
            throw new CryptoException(e);
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Arrays.hashCode(salt);
        result = 31 * result + Arrays.hashCode(iv);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (obj instanceof CryptoCodec) {
            CryptoCodec other = (CryptoCodec) obj;
            return Arrays.equals(salt, other.salt) && Arrays.equals(iv, other.iv);
        }

        return false;
    }
}
