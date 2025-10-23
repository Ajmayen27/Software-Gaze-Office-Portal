package com.ajmayen.softwaregazeportal.model;

import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    @Column(unique = true)
    private String password;

    @NotNull
    private String role;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String designation;
}
