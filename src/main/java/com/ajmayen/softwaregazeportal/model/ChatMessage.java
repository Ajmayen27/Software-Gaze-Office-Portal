package com.ajmayen.softwaregazeportal.model;

import lombok.Data;

import java.awt.*;
import java.time.LocalDateTime;

@Data
public class

ChatMessage {

    private MessageType messageType;
    private String content;
    private String sender;
    private String receiver;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
