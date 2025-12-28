package com.ajmayen.softwaregazeportal.service;

import com.ajmayen.softwaregazeportal.model.Expense;
import com.ajmayen.softwaregazeportal.model.User;
import com.ajmayen.softwaregazeportal.repository.ExpenseRepository;
import com.ajmayen.softwaregazeportal.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    public MyUserDetailsService(UserRepository userRepository, ExpenseRepository expenseRepository) {
        this.userRepository = userRepository;

        this.expenseRepository = expenseRepository;
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


    public Expense updateExpense(Expense expense, Long id, MultipartFile file) throws IOException {
        Expense expense1 = expenseRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + id));
        expense1.setBillType(expense.getBillType());
        expense1.setDate(expense.getDate());
        expense1.setAmount(expense.getAmount());
        expense1.setComment(expense.getComment());
        expense1.setTag(expense.getTag());
        expense1.setScreenshot(file.getBytes());
        return expenseRepository.save(expense1);
    }



    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


}
