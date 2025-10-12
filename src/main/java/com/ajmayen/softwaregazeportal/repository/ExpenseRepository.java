package com.ajmayen.softwaregazeportal.repository;

import com.ajmayen.softwaregazeportal.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
