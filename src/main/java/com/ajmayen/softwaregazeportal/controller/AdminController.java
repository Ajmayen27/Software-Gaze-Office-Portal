package com.ajmayen.softwaregazeportal.controller;



import com.ajmayen.softwaregazeportal.model.Expense;
import com.ajmayen.softwaregazeportal.model.User;
import com.ajmayen.softwaregazeportal.repository.ExpenseRepository;
import com.ajmayen.softwaregazeportal.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/admin")
@CrossOrigin(origins = "*")
public class AdminController {


    private final UserRepository userRepository;

    private final ExpenseRepository expenseRepository;

    public AdminController(UserRepository userRepository, ExpenseRepository expenseRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }


    @GetMapping("/employees")
    public List<User> getEmployees() {
        return userRepository.findAll();
    }


    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
         userRepository.deleteById(id);
         return ResponseEntity.ok("User has been deleted successfully");
    }

    @PostMapping("/expense")
    public Expense  addExpense(@RequestBody Expense expense){
        return expenseRepository.save(expense);
    }


    @GetMapping("/expenses")
    public List<Expense> getAllExpenses(){
        return expenseRepository.findAll();
    }


    @GetMapping("/expenses/monthly")
    public Double getMonthlyExpenses(){
        return expenseRepository.findAll().stream()
                .filter(e -> e.getDate().getMonth() == java.time.LocalDate.now().getMonth())
                .mapToDouble(Expense :: getAmount)
                .sum();
    }


    @GetMapping("/expenses/yearly")
    public Double getYearlyExpenses(){
        return expenseRepository.findAll().stream()
                .filter(e -> e.getDate().getYear() == java.time.LocalDate.now().getYear())
                .mapToDouble(Expense::getAmount)
                .sum();
     }
}
