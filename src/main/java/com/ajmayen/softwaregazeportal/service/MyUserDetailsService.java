package com.ajmayen.softwaregazeportal.service;

import com.ajmayen.softwaregazeportal.model.User;
import com.ajmayen.softwaregazeportal.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);

    }

    public User updateUser(User user,Long id) {
        User users = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + id));
        users.setUsername(user.getUsername());
        users.setPassword(encoder().encode(user.getPassword()));
        users.setRole(user.getRole());
        users.setDesignation(user.getDesignation());
        users.setEmail(user.getEmail());
        return userRepository.save(users);
    }

    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


}
