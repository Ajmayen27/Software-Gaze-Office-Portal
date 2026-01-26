package com.ajmayen.softwaregazeportal.service;

import com.ajmayen.softwaregazeportal.model.Attendance;
import com.ajmayen.softwaregazeportal.model.User;
import com.ajmayen.softwaregazeportal.repository.AttendanceRepository;
import com.ajmayen.softwaregazeportal.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }


    public String addAttendance(String employeeUseraname, Optional<LocalDateTime> punchIn , Optional<LocalDateTime> punchOut, String comment, String adminUsername) {

        User admin = userRepository.findByUsername(adminUsername).orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        User employee = userRepository.findByUsername(employeeUseraname).orElseThrow(() -> new UsernameNotFoundException("Employee not found"));



        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAdmin(admin);
        attendance.setPunchIn(punchIn.orElse(null));
        attendance.setPunchOut(punchOut.orElse(null));
        attendance.setComment(comment);
        LocalDate date = punchIn
                .map(LocalDateTime::toLocalDate)
                .orElseGet(() ->
                        punchOut.map(LocalDateTime::toLocalDate)
                                .orElse(LocalDate.now()) // fallback
                );

        attendance.setDate(date);;

        attendanceRepository.save(attendance);



        return "Attendance added successfully of Employee " + employeeUseraname;
    }


    public List<Map<String, Object>> getAttendanceSummaryAll(int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23,59);

        List<Attendance> attendances = attendanceRepository.findAllByPunchInBetween(start,end);

        Map<User, Long> attendanceCount = attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getEmployee, Collectors.counting()));

        List<Map<String, Object>> summary = new ArrayList<>();

        for (Map.Entry<User, Long> entry : attendanceCount.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("employeeUsername", entry.getKey().getUsername());
            data.put("daysPresent", entry.getValue());
            data.put("month", month);
            data.put("year", year);
            summary.add(data);
        }


        return summary;
    }



    public Map<String, Object> getIndividualAttendance(String employeeUsername, int month, int year) {

        User employee = userRepository.findByUsername(employeeUsername)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59);




        List<Attendance> attendances = attendanceRepository
                .findAllByEmployeeAndPunchInBetween(employee, start, end);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<Map<String, Object>> attendanceList = attendances.stream()
                .map(a -> {
                    Map<String, Object> map = new HashMap<>();

                    // Extract only the day from LocalDate
                    int day = a.getDate().getDayOfMonth();
                    map.put("day", day);

                    // Show only TIME from punchIn & punchOut
                    map.put("punchIn", a.getPunchIn() != null ? a.getPunchIn().format(timeFormatter) : null);
                    map.put("punchOut", a.getPunchOut() != null ? a.getPunchOut().format(timeFormatter) : null);
                    map.put("comment", a.getComment());

                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("month", month);
        result.put("employee", employeeUsername);
        result.put("attendances", attendanceList);

        return result;
    }







}
