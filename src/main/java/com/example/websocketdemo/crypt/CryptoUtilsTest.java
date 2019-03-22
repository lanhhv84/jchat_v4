package com.example.websocketdemo.crypt;

import java.io.File;

/**
 * A tester for the CryptoUtils class.
 * @author www.codejava.net
 *
 */
public class CryptoUtilsTest {
    public static void main(String[] args) {
        File inputFile = new File("document.txt");
        File encrypt = new File("asymmetric\\encrypt");
        File decrypt = new File("asymmetric\\decrypt");
        try {
            CryptoUtils.asymmetricEncrypt(inputFile, encrypt);
            CryptoUtils.asymmetricDecrypt(encrypt, decrypt);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}