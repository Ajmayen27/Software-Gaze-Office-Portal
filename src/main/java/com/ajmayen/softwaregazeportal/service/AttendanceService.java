package com.ajmayen.softwaregazeportal.service;

import com.ajmayen.softwaregazeportal.model.Attendance;
import com.ajmayen.softwaregazeportal.model.User;
import com.ajmayen.softwaregazeportal.repository.AttendanceRepository;
import com.ajmayen.softwaregazeportal.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.round;


@Service
public class AttendanceService {

    private static final double FIXED_OFFICE_HOURS = 8.0;
    private static final double GRACE_HOURS = 1.0;

    private String formatMinutes(long minutes) {

        long hh = minutes / 60;
        long mm = minutes % 60;

        return String.format("%02d:%02d", hh, mm);
    }



    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }


    public Map<String, Object> addAttendance(
            String employeeUsername,
            Optional<LocalDateTime> punchIn,
            Optional<LocalDateTime> punchOut,
            String comment,
            String adminUsername
    ) {

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        User employee = userRepository.findByUsername(employeeUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

        LocalDate date = punchIn
                .map(LocalDateTime::toLocalDate)
                .orElseGet(() ->
                        punchOut.map(LocalDateTime::toLocalDate)
                                .orElse(LocalDate.now())
                );

        if (attendanceRepository.existsByEmployeeAndDate(employee, date)) {
            throw new RuntimeException("Attendance already exists for this date");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAdmin(admin);
        attendance.setPunchIn(punchIn.orElse(null));
        attendance.setPunchOut(punchOut.orElse(null));
        attendance.setComment(comment);
        attendance.setDate(date);

        // ✅ Minutes Calculation
        long totalMinutesWorked = 0;
        long overtimeMinutes = 0;

        if (punchIn.isPresent() && punchOut.isPresent()) {

            totalMinutesWorked = Duration
                    .between(punchIn.get(), punchOut.get())
                    .toMinutes();

            // Fixed office = 8 hours = 480 min
            long fixedOfficeMinutes = 8 * 60;

            // Grace = 1 hour = 60 min
            long graceMinutes = 1 * 60;

            long threshold = fixedOfficeMinutes + graceMinutes;

            if (totalMinutesWorked > threshold) {
                overtimeMinutes = totalMinutesWorked - threshold;
            }
        }

        // ✅ Store in DB
        attendance.setTotalWorkingMinutes(totalMinutesWorked);
        attendance.setOverTimeMinutes(overtimeMinutes);

        attendanceRepository.save(attendance);

        // ✅ Response in HH:mm format
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Attendance added successfully");
        response.put("employee", employeeUsername);
        response.put("date", date);

        response.put("totalWorkingHours", formatMinutes(totalMinutesWorked));
        response.put("overTime", formatMinutes(overtimeMinutes));

        return response;
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

        List<Attendance> attendances =
                attendanceRepository.findAllByEmployeeAndPunchInBetween(employee, start, end);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<Map<String, Object>> attendanceList = attendances.stream()
                .map(a -> {

                    Map<String, Object> map = new HashMap<>();

                    map.put("day", a.getDate().getDayOfMonth());

                    map.put("punchIn", a.getPunchIn() != null
                            ? a.getPunchIn().format(timeFormatter)
                            : null);

                    map.put("punchOut", a.getPunchOut() != null
                            ? a.getPunchOut().format(timeFormatter)
                            : null);

                    map.put("comment", a.getComment());

                    // ✅ Correct Output
                    map.put("totalWorkingHours",
                            formatMinutes(a.getTotalWorkingMinutes()));

                    map.put("overTime",
                            formatMinutes(a.getOverTimeMinutes()));

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
