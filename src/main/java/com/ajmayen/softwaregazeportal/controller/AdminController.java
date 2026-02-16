package com.ajmayen.softwaregazeportal.controller;



import com.ajmayen.softwaregazeportal.model.Expense;
import com.ajmayen.softwaregazeportal.model.User;
import com.ajmayen.softwaregazeportal.repository.AttendanceRepository;
import com.ajmayen.softwaregazeportal.repository.ExpenseRepository;
import com.ajmayen.softwaregazeportal.repository.UserRepository;
import com.ajmayen.softwaregazeportal.service.AttendanceService;
import com.ajmayen.softwaregazeportal.service.MyUserDetailsService;
import com.ajmayen.softwaregazeportal.service.PdfReportService;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "https://softwaregazeportal.netlify.app"
        },
        allowCredentials = "true"
)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    private final UserRepository userRepository;

    private final ExpenseRepository expenseRepository;

    private final MyUserDetailsService  myUserDetailsService;

    private final AttendanceService  attendanceService;

    private final PdfReportService pdfReportService;

    private final AttendanceRepository attendanceRepository;

    public AdminController(UserRepository userRepository, ExpenseRepository expenseRepository, MyUserDetailsService myUserDetailsService, AttendanceService attendanceService, PdfReportService pdfReportService, AttendanceRepository attendanceRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.myUserDetailsService = myUserDetailsService;
        this.attendanceService = attendanceService;
        this.pdfReportService = pdfReportService;
        this.attendanceRepository = attendanceRepository;
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
    public Expense addExpense(
            @RequestPart("expense") Expense expense,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        if (file != null && !file.isEmpty()) {
            expense.setScreenshot(file.getBytes());
            expense.setImageType(file.getContentType());
        }
        return expenseRepository.save(expense);
    }


    @GetMapping("/expenses/report/CurrentMonth")
    public void downloadMonthlyReport(HttpServletResponse response) throws Exception {
        List<Expense> monthlyExpenses = expenseRepository.findAll().stream()
                .filter(e -> e.getDate().getMonth() == LocalDate.now().getMonth() &&
                        e.getDate().getYear() == LocalDate.now().getYear())
                .toList();

        pdfReportService.exportAsPdf(monthlyExpenses, "Monthly-Expense-Report", response);
    }




    @GetMapping("/expenses/report/monthly/tag")
    public void downloadMonthlyReport(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam String tag,
            HttpServletResponse response
    ) throws Exception {

        List<Expense> monthlyExpenses = expenseRepository.findAll().stream()
                .filter(e ->
                        e.getDate().getMonthValue() == month &&
                                e.getDate().getYear() == year &&
                                e.getTag().equals(tag)
                )
                .toList();

        pdfReportService.exportAsPdf(
                monthlyExpenses,
                "Monthly-Expense-Report-" + month + "-" + year,
                response
        );
    }


    @GetMapping("/expenses/report/monthly")
    public void downloadMonthlyReport(
            @RequestParam int month,
            @RequestParam int year,
            HttpServletResponse response
    ) throws Exception {

        List<Expense> monthlyExpenses = expenseRepository.findAll().stream()
                .filter(e ->
                        e.getDate().getMonthValue() == month &&
                                e.getDate().getYear() == year
                )
                .toList();

        pdfReportService.exportAsPdf(
                monthlyExpenses,
                "Monthly-Expense-Report-" + month + "-" + year,
                response
        );

    }



        @GetMapping("/expenses/report/yearly")
    public void downloadYearlyReport(HttpServletResponse response) throws Exception {
        List<Expense> yearlyExpenses = expenseRepository.findAll().stream()
                .filter(e -> e.getDate().getYear() == LocalDate.now().getYear())
                .toList();

        pdfReportService.exportAsPdf(yearlyExpenses, "Yearly-Expense-Report", response);
    }



    @GetMapping("/expenses")
    public List<Expense> getAllExpenses(){
        return expenseRepository.findAll();
    }


    @PutMapping("/expense/update/{id}")
    public ResponseEntity<?> updateExpense(@RequestPart("expense") Expense expense,
                                           @RequestPart(value = "file", required = false) MultipartFile file,
                                           @PathVariable Long id) throws IOException {
        return ResponseEntity.ok(myUserDetailsService.updateExpense(expense,id,file));
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



    @PostMapping("/add/attendance")
    public Map<String, String> addAttendance(@RequestBody Map<String, Object> body, Authentication authentication) {
        String adminUsername = authentication.getName();
        String employeeUsername = body.get("employeeUsername").toString();
        String comment = body.get("comment") != null ? body.get("comment").toString() : null;

        LocalDateTime punchIn = body.get("punchIn") != null
                ? LocalDateTime.parse(body.get("punchIn").toString())
                : null;
        LocalDateTime punchOut = body.get("punchOut") != null
                ? LocalDateTime.parse(body.get("punchOut").toString()) : null;

        String message = attendanceService.addAttendance(employeeUsername, Optional.ofNullable(punchIn) , Optional.ofNullable(punchOut), comment, adminUsername).toString();
        return Map.of("message", message);
    }


    @DeleteMapping("/attendance/delete")
    public ResponseEntity<Map<String,Object>> deleteAttendance(
            @RequestParam String username,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day
    ){

         Map<String,Object> result = attendanceService.deleteAttendance(username,year,month,day);
         return ResponseEntity.ok(result);
    }



    @GetMapping("/attendance/summary")
    public List<Map<String, Object>> getAllSummary(@RequestParam int month, @RequestParam int year) {
        return attendanceService.getAttendanceSummaryAll(month, year);
    }


    @GetMapping("/attendance/summary/{username}")
    public Map<String, Object> getIndividualSummary(@PathVariable String username,
                                                    @RequestParam int month,
                                                    @RequestParam int year) {
        return attendanceService.getIndividualAttendance(username, month, year);
    }



    @PutMapping("/attendance/grace-period")
    public ResponseEntity<Map<String, Object>> updateGrace(
            @RequestParam double graceHours
    ) {
        return ResponseEntity.ok(attendanceService.updateGracePeriod(graceHours));
    }

    @GetMapping("/grace-period/latest")
    public ResponseEntity<Map<String, Object>> getLatestGracePeriod() {

        return ResponseEntity.ok(attendanceService.getLatestGracePeriod());
    }

}
