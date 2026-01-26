package com.ajmayen.softwaregazeportal.model;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "date"})
})
@Data
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id",nullable = false)
    private User employee;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id",nullable = false)
    private User admin;

    @Column(name ="punch_in",nullable = true)
    private  LocalDateTime punchIn;

    @Column(name = "punch_out",nullable = true)
    private LocalDateTime punchOut;


    @Column(name = "date",nullable = false)
    private LocalDate date;


    private String comment;


    private double TotalWorkingHours;


    private double OverTime;



}
