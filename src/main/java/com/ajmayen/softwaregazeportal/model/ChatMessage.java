package com.ajmayen.softwaregazeportal.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {

    private String from;
    private String to;
    private String text;
    private LocalDateTime timestamp;
}
