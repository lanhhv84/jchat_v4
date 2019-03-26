package com.example.websocketdemo.controller;

import com.example.websocketdemo.constant.Constant;
import com.example.websocketdemo.constant.ServerAsymmetricKey;
import com.example.websocketdemo.crypt.Crypto;
import com.example.websocketdemo.model.Key;
import com.example.websocketdemo.model.User;
import com.example.websocketdemo.service.KeyService;
import com.example.websocketdemo.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Calendar;

@Controller
@RequestMapping("/key")
public class KeyController {

    @Autowired
    UserService userService;

    @Autowired
    KeyService keyService;

    @Autowired
    Crypto crypto;

    @RequestMapping("/server")
    public ResponseEntity<?> getServerPublicKey() {
        return ResponseEntity.ok(new String(ServerAsymmetricKey.getPublicKey()));
    }

    @RequestMapping("/user")
    public ResponseEntity<?> getUserKey(@RequestParam("username") String username) {
        User user = userService.findOne(username);
        return ResponseEntity.ok(new String(user.getLastPublic().toPublic().getEncoded()));
    }

    @RequestMapping("/aes")
    public ResponseEntity<?> getAES(@RequestParam("username") String username) {
        User user = userService.findOne(username);
        PublicKey publicKey = user.getLastPublic().toPublic();
        byte[] rsaEncryptedKey = (crypto.encrypt(Base64.encodeBase64(ServerAsymmetricKey.getAESKey()), "RSA", publicKey)).get("value");
        String aesStringKey = Base64.encodeBase64String(rsaEncryptedKey);
        System.out.println("Length of AES Key: " + String.valueOf(Base64.encodeBase64String(ServerAsymmetricKey.getAESKey()).length()));
        System.out.println("AES Encrypted key");
        System.out.println(aesStringKey);
        return ResponseEntity.ok(aesStringKey);
    }

    @RequestMapping("/register")
    public ResponseEntity<?> register(@RequestParam("key") String key, @RequestParam("username") String username) {
        try {
            User owner = userService.findOne(username);
            byte[] publicKey = key.getBytes("UTF-8");
            Key publicKeyModel = new Key(publicKey, true, true, owner);
            publicKeyModel.setCreationTime(Calendar.getInstance().getTime());
            keyService.add(publicKeyModel);

        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        return ResponseEntity.ok("");
    }




}
