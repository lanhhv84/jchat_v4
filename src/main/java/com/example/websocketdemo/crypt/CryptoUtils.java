package com.example.websocketdemo.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
/**
 * A utility class that encrypts or decrypts a file.
 * @author www.codejava.net
 *
 */
public class CryptoUtils {
    private static final String SYMMETRIC_ALGORITHM = "AES";
    private static final String SYMMETRIC_TRANSFORMATION = "AES";
    private static final String ASYMMETRIC_ALGORITHM = "RSA";
    private static final String ASYMMETRIC_TRANSFORMATION = "RSA";
    private PrivateKey pvt;
    private PublicKey pub;

    public void keyGenerator() throws CryptoException {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(ASYMMETRIC_ALGORITHM);
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            pvt = kp.getPrivate();
            pub = kp.getPublic();

        } catch (NoSuchAlgorithmException ex) {
            throw new CryptoException("Error key generating", ex);
        }
    }

    public void symmetricEncrypt(File inputFile, File outputFile) throws CryptoException {
        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            String key = RandomStringUtils.randomAscii(16);

            Key secretKey = new SecretKeySpec(key.getBytes(), SYMMETRIC_ALGORITHM);
            Cipher cipher = Cipher.getInstance(SYMMETRIC_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] outputBytes = cipher.doFinal(inputBytes);
            byte[] result = ArrayUtils.addAll(outputBytes, key.getBytes());

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(result);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException| BadPaddingException
                | IllegalBlockSizeException |IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }

    }
    public void symmetricDecrypt(File inputFile, File outputFile) throws CryptoException {
        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            byte[] cipherText = Arrays.copyOfRange(inputBytes, 0, inputBytes.length - 16);
            byte[] key = Arrays.copyOfRange(inputBytes, inputBytes.length - 16, inputBytes.length);

            Key secretKey = new SecretKeySpec(key, SYMMETRIC_ALGORITHM);
            Cipher cipher = Cipher.getInstance(SYMMETRIC_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] outputBytes = cipher.doFinal(cipherText);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException| BadPaddingException
                | IllegalBlockSizeException |IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    public void asymmetricEncrypt(File inputFile, File outputFile) throws CryptoException {
        this.keyGenerator();
        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] inputBytes = new byte[245];
            Cipher cipher = Cipher.getInstance(ASYMMETRIC_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, pub);
            int len;
            while ((len = inputStream.read(inputBytes)) != -1) {
                if (len < 245) Arrays.fill(inputBytes, len, 245, (byte)0);
                byte[] outputBytes = cipher.doFinal(inputBytes);
                outputStream.write(outputBytes);
            }
            inputStream.close();
            outputStream.close();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException| BadPaddingException
                | IllegalBlockSizeException |IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
    public void asymmetricDecrypt(File inputFile, File outputFile) throws CryptoException {
        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] inputBytes = new byte[256];
            Cipher cipher = Cipher.getInstance(ASYMMETRIC_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, pvt);
            int len;
            while ((len = inputStream.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.doFinal(inputBytes);
                outputStream.write(outputBytes);
            }
            inputStream.close();
            outputStream.close();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException| BadPaddingException
                | IllegalBlockSizeException |IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    public PrivateKey getPvt() {
        return pvt;
    }

    public void setPvt(PrivateKey pvt) {
        this.pvt = pvt;
    }

    public PublicKey getPub() {
        return pub;
    }

    public void setPub(PublicKey pub) {
        this.pub = pub;
    }
}