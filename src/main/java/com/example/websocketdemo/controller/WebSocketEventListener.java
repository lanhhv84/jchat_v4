package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import com.example.websocketdemo.model.ChatMessage.MessageType;
import com.example.websocketdemo.model.Key;
import com.example.websocketdemo.model.User;
import com.example.websocketdemo.service.KeyService;
import com.example.websocketdemo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Predicate;

import static java.lang.String.format;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private KeyService keyService;
    @Autowired
    private UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection.");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomId = (String) headerAccessor.getSessionAttributes().get("room_id");
        if (username != null) {
            Date now = Calendar.getInstance().getTime();

            logger.info("User Disconnected: " + username);

            // Alter expired time
            User user = userService.findOne(username);

            Key publicKey = user.getLastPublic();
            Key privateKey = user.getLastPrivate();


            publicKey.setExpiredTime(Calendar.getInstance().getTime());
            privateKey.setExpiredTime(Calendar.getInstance().getTime());

            System.out.println("Key expired");

            userService.add(user);
            keyService.add(publicKey);
            keyService.add(privateKey);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
        }
    }
}
