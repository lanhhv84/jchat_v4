package com.example.websocketdemo.crypt;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;


@Service
public class Crypto {
    public static final byte[] initVector = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public byte[] encrypt(byte[] plainData, String algorithm, String key) {
        byte[] encrypted = null;
        Cipher cipher = null;
        try {
            switch (algorithm) {
                case "AES":
                    Key secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
                    IvParameterSpec iv = new IvParameterSpec(initVector);
                    cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
                    break;
                case "RSA":
                    KeyPair keys = KeyPairGenerator.getInstance("RSA").generateKeyPair();
                    cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, keys.getPublic());
                    break;
                case "Blowfish":
                    byte[] KeyData = key.getBytes("UTF-8");
                    SecretKeySpec KS = new SecretKeySpec(KeyData, "Blowfish");
                    cipher = Cipher.getInstance("Blowfish");
                    cipher.init(Cipher.ENCRYPT_MODE, KS);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            encrypted = cipher.doFinal(plainData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Base64.encodeBase64(encrypted);
    }

    public byte[] decrypt(byte[] encrypted, String algorithm, String key) throws InvalidAlgorithmParameterException, UnsupportedEncodingException {
        Cipher cipher = null;
        Key secretKey = null;


        try {
            switch (algorithm) {
                case "RSA":
                    secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
                    cipher = Cipher.getInstance("AES");
                    break;
                case "AES":
                    secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
                    cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

                    break;
                case "BlowFish":
                    secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "Blowfish");
                    cipher = Cipher.getInstance("Blowfish");
                    break;
            }
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                UnsupportedEncodingException
                ex) {

        }
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return cipher.doFinal(Base64.decodeBase64(encrypted));
        } catch (InvalidKeyException |
                IllegalBlockSizeException |
                BadPaddingException | NullPointerException ex) {
            System.out.println(ex);
            return null;
        }

    }
}
