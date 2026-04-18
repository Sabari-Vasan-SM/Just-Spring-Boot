package com.hrms.repository;

import com.hrms.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeId(Long employeeId);
    Attendance findByEmployeeIdAndDate(Long employeeId, LocalDate date);
}
