package com.ajmayen.softwaregazeportal.controller;


import com.ajmayen.softwaregazeportal.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;


    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }



    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        simpMessagingTemplate.convertAndSend("/topic/public", chatMessage);
    }


    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        simpMessagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(),
                "/queue/message",
                chatMessage
        );
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor  headerAccessor) {

        if(headerAccessor.getSessionAttributes() != null){
            headerAccessor.getSessionAttributes().put("username",chatMessage.getSender());
        }

        simpMessagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
}
