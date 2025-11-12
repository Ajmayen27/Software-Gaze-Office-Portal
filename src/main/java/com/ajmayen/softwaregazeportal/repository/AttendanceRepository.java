package com.ajmayen.softwaregazeportal.repository;

import com.ajmayen.softwaregazeportal.model.Attendance;
import com.ajmayen.softwaregazeportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllByPunchInBetween(LocalDateTime start, LocalDateTime end);
    List<Attendance> findAllByEmployeeAndPunchInBetween(User employee, LocalDateTime start, LocalDateTime end);
}
