package com.example.websocketdemo.crypt;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.util.Map;

/**
 * A tester for the CryptoUtils class.
 *
 * @author www.codejava.net
 */
public class CryptoUtilsTest {
    public static void main(String[] args) throws UnsupportedEncodingException, IOException, InvalidAlgorithmParameterException {
        File file = new File("/home/hvlpr/Desktop/1611731_TNMT.pdf");
        byte[] data =  Files.readAllBytes(file.toPath());
        Crypto crypto = new Crypto();
        System.out.println(data.length);
        System.out.println(file.length());
        Map<String, byte[]> a = crypto.encrypt(data, "AES");
        byte[] encrypted = a.get("value");
        byte[] plain = crypto.decrypt(encrypted, "AES", a.get("key"));
        System.out.println(plain.length);



    }
}