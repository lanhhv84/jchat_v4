package com.example.websocketdemo.crypt;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;


@Service
public class Crypto {
    public static final byte[] initVector = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public Map<String, byte[]> encrypt(byte[] plainData, String algorithm, byte[] publicKey) {
        System.out.println(plainData.length);
        byte[] encrypted = null;
        Map<String, byte[]> values = new HashMap<>();
        Cipher cipher = null;
        Key secretKey = null;
        try {
            switch (algorithm) {
                case "AES":
                    secretKey = KeyGenerator.getInstance("AES").generateKey();
                    IvParameterSpec iv = new IvParameterSpec(initVector);
                    values.put("key", secretKey.getEncoded());
                    System.out.println("Decrypting with AES with key: " + new String(secretKey.getEncoded()));
                    cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    break;
                case "RSA":
                    KeyPair keys = KeyPairGenerator.getInstance("RSA").generateKeyPair();
                    cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, keys.getPublic());
                    break;
                case "Blowfish":
                    secretKey = KeyGenerator.getInstance("Blowfish").generateKey();
                    cipher = Cipher.getInstance("Blowfish");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            encrypted = cipher.doFinal(plainData);
            values.put("value", Base64.encodeBase64(encrypted));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return values;
    }

    public byte[] decrypt(byte[] encrypted, String algorithm, byte[] key, byte[] privateKey) throws InvalidAlgorithmParameterException, UnsupportedEncodingException {
        Cipher cipher = null;
        Key secretKey = null;
        encrypted = Base64.decodeBase64(encrypted);

        try {
            switch (algorithm) {
                case "RSA":
                    secretKey = KeyGenerator.getInstance("AES").generateKey();

                    cipher = Cipher.getInstance("AES");
                    break;
                case "AES":
                    System.out.println("Decrypting with AES with key: " + new String(key));
                    secretKey = new SecretKeySpec(key, "AES");
                    cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

                    break;
                case "BlowFish":
                    secretKey = new SecretKeySpec(key, "Blowfish");
                    cipher = Cipher.getInstance("Blowfish");
                    break;
            }
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException
                ex) {
            ex.printStackTrace();
        }
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            System.out.println(cipher.doFinal(encrypted).length);
            return cipher.doFinal(encrypted);
        } catch (InvalidKeyException |
                IllegalBlockSizeException |
                BadPaddingException | NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }

    }
}
