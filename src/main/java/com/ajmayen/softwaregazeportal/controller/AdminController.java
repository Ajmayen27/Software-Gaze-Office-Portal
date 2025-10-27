package com.ajmayen.softwaregazeportal.controller;



import com.ajmayen.softwaregazeportal.model.Expense;
import com.ajmayen.softwaregazeportal.model.User;
import com.ajmayen.softwaregazeportal.repository.ExpenseRepository;
import com.ajmayen.softwaregazeportal.repository.UserRepository;
import com.ajmayen.softwaregazeportal.service.MyUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    private final UserRepository userRepository;

    private final ExpenseRepository expenseRepository;

    private final MyUserDetailsService  myUserDetailsService;

    public AdminController(UserRepository userRepository, ExpenseRepository expenseRepository, MyUserDetailsService myUserDetailsService) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.myUserDetailsService = myUserDetailsService;
    }


    @GetMapping("/employees")
    public List<User> getEmployees() {
        return userRepository.findAll();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User user,@PathVariable Long id) {
        return ResponseEntity.ok(myUserDetailsService.updateUser(user,id));
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


    @PutMapping("/expense/update/{id}")
    public ResponseEntity<?> updateExpense(@RequestBody Expense expense,@PathVariable Long id){
        return ResponseEntity.ok(myUserDetailsService.updateExpense(expense,id));
    }

    @DeleteMapping("/expense/delete/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id){
        expenseRepository.deleteById(id);
        return ResponseEntity.ok("Expense has been deleted successfully");
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
