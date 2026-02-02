package com.ajmayen.softwaregazeportal.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "grace_period_setting")
@Data
public class GracePeriodSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="grace_hours", nullable = false)
    private double graceHours;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;




}
