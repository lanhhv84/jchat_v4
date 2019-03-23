package com.example.websocketdemo.crypt;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Service
public class Hasher {

    MessageDigest digest;

    public Hasher() {
        try {
            digest = MessageDigest.getInstance("SHA-256");

        } catch (NoSuchAlgorithmException ex) {

        }

    }

    public String hash(byte[] file) {
        return Hex.encodeHexString(digest.digest(file));
    }

    public String hash(String value) {
        byte[] digestedMessage = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(digestedMessage);
    }
}
