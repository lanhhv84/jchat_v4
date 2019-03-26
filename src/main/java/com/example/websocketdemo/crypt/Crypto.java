package com.example.websocketdemo.crypt;

import com.example.websocketdemo.exception.FileStorageException;
import com.example.websocketdemo.property.FileStorageProperties;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Service
public class Crypto {
    public static final byte[] initVector = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public static final int blockSize = 128;


    public Map<String, byte[]> encrypt(byte[] plainData, String algorithm, PublicKey publicKey) {
        System.out.println(plainData.length);
        byte[] encrypted = null;
        Map<String, byte[]> values = new HashMap<>();
        Cipher cipher = null;
        System.out.println(algorithm);
        Key secretKey = null;
        try {
            switch (algorithm) {
                case "AES":
                    secretKey = KeyGenerator.getInstance("AES").generateKey();
                    IvParameterSpec iv = new IvParameterSpec(initVector);
                    values.put("key", secretKey.getEncoded());
                    System.out.println("Encrypting with AES with key: " + new String(secretKey.getEncoded()));
                    cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    encrypted = cipher.doFinal(plainData);
                    break;
                case "AES2":
                    secretKey = KeyGenerator.getInstance("AES").generateKey();
                    values.put("key", secretKey.getEncoded());
                    System.out.println("Encrypting with AES with key: " + new String(secretKey.getEncoded()));
                    cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    encrypted = cipher.doFinal(plainData);
                case "RSA":
//                    cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//                    ByteArrayInputStream bis = new ByteArrayInputStream(plainData);
//                    File tempFile = new File("./uploads/tempFile");
//                    FileOutputStream tempOutputStream = new FileOutputStream(tempFile);
//                    byte[] blockData = new byte[Crypto.blockSize];
//                    int len = 0;
//                    byte[] encryptedBlock = null;
//                    while ((len = bis.read(blockData)) != -1) {
//                        if (len < Crypto.blockSize) Arrays.fill(blockData, len, Crypto.blockSize, (byte) 0);
//                        encryptedBlock = cipher.doFinal(blockData);
//                        tempOutputStream.write(encryptedBlock);
//                    }
//                    encrypted = Files.readAllBytes(tempFile.toPath());
                      cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                      encrypted = cipher.doFinal(plainData);

                    break;
                case "Blowfish":
                    secretKey = KeyGenerator.getInstance("Blowfish").generateKey();
                    cipher = Cipher.getInstance("Blowfish");
                    values.put("key", secretKey.getEncoded());
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    encrypted = cipher.doFinal(plainData);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {

            values.put("value", encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return values;
    }

    public byte[] decrypt(byte[] encrypted, String algorithm, byte[] key, PrivateKey privateKey)  {
        Cipher cipher = null;
        SecretKey secretKey = null;
        encrypted = Base64.decodeBase64(encrypted);
        byte[] plainData = null;

        try {
            switch (algorithm) {
                case "RSA":
                    cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    ByteArrayInputStream bis = new ByteArrayInputStream(encrypted);
                    File tempFile = new File("./uploads/tempFile");
                    tempFile.createNewFile();
                    FileOutputStream tempOutPutStream = new FileOutputStream(tempFile);
                    byte[] blockData = new byte[256];
                    byte[] encryptedBlock = null;
                    int length = 0;
                    while ((length = bis.read(blockData)) != -1) {
                        encryptedBlock = cipher.doFinal(blockData);
                        tempOutPutStream.write(encryptedBlock);
                    }
                    plainData = Files.readAllBytes(tempFile.toPath());


                    break;
                case "AES2":
                    System.out.println("Decrypting with AES with key: " + new String(key));
                    secretKey = new SecretKeySpec(key, "AES/ECB/PKCS5Padding");
                    cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                    plainData = cipher.doFinal(encrypted);
                    break;
                case "AES":
                    System.out.println("Decrypting with AES with key: " + new String(key));
                    secretKey = new SecretKeySpec(key, "AES");
                    cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                    plainData = cipher.doFinal(encrypted);
                    break;
                case "Blowfish":
                    secretKey = new SecretKeySpec(key, "Blowfish");
                    cipher = Cipher.getInstance("Blowfish");
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                    plainData = cipher.doFinal(encrypted);
                    break;
            }
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                NullPointerException |
                InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException
                ex) {
            ex.printStackTrace();
        }
        try {

            return plainData;
        } catch ( NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public KeyPair generateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }
}
