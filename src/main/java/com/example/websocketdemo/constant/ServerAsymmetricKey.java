package com.example.websocketdemo.constant;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class ServerAsymmetricKey {
    private static byte[] publicKey;
    private static byte[] privateKey;
    private static byte[] AESKey;
    private static byte[] BlowfishKey;

    private static void generateKey() {
        if (publicKey == null || privateKey == null) {
            try {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(2048);
                KeyPair keyPair = keyGen.generateKeyPair();
                publicKey = Base64.encodeBase64(keyPair.getPublic().getEncoded());
                privateKey = Base64.encodeBase64(keyPair.getPrivate().getEncoded());
            }
            catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }

        }

        if (AESKey == null) {
            try {
                SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
                AESKey = secretKey.getEncoded();
                System.out.println("AES");
                System.out.println(new String(AESKey));
            }
            catch (NoSuchAlgorithmException ex) {

            }
        }

        if (BlowfishKey == null) {
            try {
                SecretKey secretKey = KeyGenerator.getInstance("Blowfish").generateKey();
                BlowfishKey = secretKey.getEncoded();
            }
            catch (NoSuchAlgorithmException ex) {

            }
        }
    }

    public static byte[] getPublicKey() {
        if (publicKey == null) {
            ServerAsymmetricKey.generateKey();
        }
        return Base64.decodeBase64(publicKey);
    }

    public static void setPublicKey(byte[] publicKey) {
        ServerAsymmetricKey.publicKey = publicKey;
    }

    public static byte[] getPrivateKey() {
        if (privateKey == null) {
            ServerAsymmetricKey.generateKey();
        }
        return Base64.decodeBase64(privateKey);
    }

    public static void setPrivateKey(byte[] privateKey) {
        ServerAsymmetricKey.privateKey = privateKey;
    }

    public static byte[] getAESKey() {
        if (AESKey == null) {
            ServerAsymmetricKey.generateKey();
        }
        return Base64.decodeBase64(AESKey);
    }

    public static void setAESKey(byte[] AESKey) {
        ServerAsymmetricKey.AESKey = AESKey;
    }

    public static byte[] getBlowfishKey() {
        if (BlowfishKey == null) {
            ServerAsymmetricKey.generateKey();
        }
        return Base64.decodeBase64(BlowfishKey);
    }

    public static void setBlowfishKey(byte[] blowfishKey) {
        BlowfishKey = blowfishKey;
    }
}
