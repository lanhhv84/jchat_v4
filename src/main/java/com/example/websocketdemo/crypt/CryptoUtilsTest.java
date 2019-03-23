package com.example.websocketdemo.crypt;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;

/**
 * A tester for the CryptoUtils class.
 *
 * @author www.codejava.net
 */
public class CryptoUtilsTest {
    public static void main(String[] args) throws UnsupportedEncodingException, InvalidAlgorithmParameterException {
        String test = "HUYNH VAN LANH";
        Crypto crypto = new Crypto();
        String key = RandomStringUtils.randomAscii(16);
        System.out.println(new String(crypto.decrypt(crypto.encrypt(test.getBytes("UTF-8"), "AES", key), "AES", key)));
    }
}