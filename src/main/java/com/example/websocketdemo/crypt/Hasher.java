package com.example.websocketdemo.crypt;

import org.springframework.context.annotation.Bean;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import org.apache.commons.codec.binary.Hex;

public class Hasher {

    MessageDigest digest;

    public Hasher() {
        try {
            digest = MessageDigest.getInstance("SHA-256");

        }
        catch (NoSuchAlgorithmException ex) {

        }

    }

    public String hash(byte[] file) {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    public String hash(String value) {
        byte[] digestedMessage =  digest.digest(value.getBytes(StandardCharsets.UTF_8));
        String ans = Hex.encodeHexString(digestedMessage);
        return ans;
    }
}
