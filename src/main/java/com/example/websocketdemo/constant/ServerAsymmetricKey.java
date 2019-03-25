package com.example.websocketdemo.constant;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class ServerAsymmetricKey {
    private static byte[] publicKey;
    private static byte[] privateKey;

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
}
