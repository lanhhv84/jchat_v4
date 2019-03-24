package com.example.websocketdemo.controller;

import com.example.websocketdemo.crypt.CryptoException;
import com.example.websocketdemo.crypt.CryptoUtils;
import com.example.websocketdemo.crypt.Hasher;
import com.example.websocketdemo.model.Key;
import com.example.websocketdemo.model.User;
import com.example.websocketdemo.service.KeyService;
import com.example.websocketdemo.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.PublicKey;
import java.util.Calendar;
import java.util.HashMap;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    @Autowired
    KeyService keyService;


    Hasher hasher = new Hasher();

    public ResponseEntity<?> ok(boolean value) {
        HashMap<String, Boolean> values = new HashMap<>();
        values.put("value", value);
        return ResponseEntity.ok(values);
    }

    @RequestMapping("/add")
    public ResponseEntity<?> add(@RequestParam("username") String username,
                                 @RequestParam("password") String password) {
        if (userService.existsByUserName(username)) {
            return ok(false);
        } else {
            User user = new User(username, hasher.hash(password));
            userService.add(user);
            return ok(true);
        }
    }

    @RequestMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {

        if (userService.login(username, hasher.hash(password))) {

            CryptoUtils cryptoUtils = new CryptoUtils();
            // Create new key
            try {
                cryptoUtils.keyGenerator();
                PublicKey publicKey = cryptoUtils.getPub();
                byte[] key = Base64.encodeBase64(publicKey.getEncoded());
                User user = userService.findOne(username);
                Key keyModel = new Key(key, true, true, user);
                keyModel.setCreationTime(Calendar.getInstance().getTime());
                Key privateKeyModel = new Key(Base64.encodeBase64((cryptoUtils.getPvt().getEncoded())),
                                true,
                                false,
                                user);
                privateKeyModel.setCreationTime(Calendar.getInstance().getTime());
                keyService.add(keyModel);
                keyService.add(privateKeyModel);

            } catch (CryptoException ex) {

            }

            return ok(true);
        } else {
            return ok(false);
        }

    }
}
