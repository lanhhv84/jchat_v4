package com.example.websocketdemo.crypt;

import org.springframework.context.annotation.Bean;

import java.util.Calendar;

public class Hasher {
    public String hash(byte[] file) {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    public String hash(String value) {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }
}
