package com.ajmayen.softwaregazeportal.model;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Data
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id",nullable = false)
    private User employee;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id",nullable = false)
    private User admin;

    @Column(name ="punch_in",nullable = false)
    private LocalDate punchIn;

    @Column(name = "punch_out")
    private LocalDate punchOut;


    @Column(name = "date",nullable = false)
    private LocalDate date;


    private String comment;

}
