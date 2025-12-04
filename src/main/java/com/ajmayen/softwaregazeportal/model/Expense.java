package com.ajmayen.softwaregazeportal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String billType;
    private Double amount;
    private String comment;
    private String tag;
    private LocalDate date;

    @Lob
    @Column(length = 10000000)
    private byte[] screenshot;

    private String imageType;
}
