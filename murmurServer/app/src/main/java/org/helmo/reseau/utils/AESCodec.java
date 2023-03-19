package org.helmo.reseau.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

// Ressource de Monsieur Swinnen : https://www.javainterviewpoint.com/java-aes-256-gcm-encryption-and-decryption/
public class AESCodec {
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;

    private String key;

    public AESCodec(String key) {
        this.key = key;
    }

    public Cipher getCipherInstance() {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
        } catch (Exception e) {
            throw new RuntimeException("Error while getting cipher instance : " + e.getMessage());
        }
        return cipher;
    }

    public void initCipher(Cipher cipher, int encryptMode, SecretKeySpec secretKeySpec, GCMParameterSpec gcmParameterSpec) {
        try {
            cipher.init(encryptMode, secretKeySpec, gcmParameterSpec);
        } catch (Exception e) {
            throw new RuntimeException("Error while cipher initialization : " + e.getMessage());
        }
    }

    public byte[] performEncryption(Cipher cipher, String message) {
        try {
            return cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Error while performing encryption or decryption with cipher : " + e.getMessage());
        }
    }

    public byte[] performDecryption(Cipher cipher, byte[] message) {
        try {
            return cipher.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Error while performing encryption or decryption with cipher : " + e.getMessage());
        }
    }

    public String encrypt(String message) {
        if (message == null || message.isEmpty() || key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Message and key must be not null and not empty");
        }

        byte[] iv = new byte[GCM_IV_LENGTH];
        var secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = getCipherInstance();

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        initCipher(cipher, Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);

        byte[] encryptedText = performEncryption(cipher, message);

        return String.format("%s%s", Base64.getEncoder().encodeToString(encryptedText), Base64.getEncoder().encodeToString(iv));
    }

    public String decrypt(String message) {
        if (message == null || message.isEmpty() || key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Message and key must be not null and not empty");
        }

        if (message.length() <= GCM_IV_LENGTH) {
            throw new IllegalArgumentException("Your message size is incorrect (smaller than GCM_IV_LENGTH)");
        }

        message = message.replaceAll("[\r]", "");
        message = message.replaceAll("[\n]", "");

        byte[] messageToDecode = message.substring(0, message.length() - GCM_TAG_LENGTH).getBytes(StandardCharsets.UTF_8);
        byte[] ivToDecode = message.substring(message.length() - GCM_TAG_LENGTH).getBytes(StandardCharsets.UTF_8);

        byte[] messageDecoded = Base64.getDecoder().decode(messageToDecode);
        System.out.println("IV = " + ivToDecode.length);
        byte[] iv = Base64.getDecoder().decode(ivToDecode);

        Cipher cipher = getCipherInstance();

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        initCipher(cipher, Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);

        byte[] decryptedText = performDecryption(cipher, messageDecoded);

        return new String(decryptedText, StandardCharsets.UTF_8);
    }
}