package com.hrms.controller;

import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Attendance;
import com.hrms.model.Employee;
import com.hrms.repository.AttendanceRepository;
import com.hrms.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/check-in")
    public ResponseEntity<Attendance> checkIn(Authentication authentication) {
        String email = authentication.getName();
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found");
        }

        LocalDate today = LocalDate.now();
        Attendance existing = attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today);
        
        if (existing != null) {
            return ResponseEntity.badRequest().body(existing); // Already checked in
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(today);
        attendance.setCheckInTime(LocalTime.now());
        attendance.setStatus("PRESENT");
        
        return ResponseEntity.ok(attendanceRepository.save(attendance));
    }

    @PostMapping("/check-out")
    public ResponseEntity<Attendance> checkOut(Authentication authentication) {
        String email = authentication.getName();
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found");
        }

        LocalDate today = LocalDate.now();
        Attendance existing = attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today);
        
        if (existing == null) {
            return ResponseEntity.badRequest().build(); // Not checked in
        }

        existing.setCheckOutTime(LocalTime.now());
        return ResponseEntity.ok(attendanceRepository.save(existing));
    }

    @GetMapping("/my-records")
    public ResponseEntity<List<Attendance>> getMyAttendance(Authentication authentication) {
        Employee employee = employeeRepository.findByEmail(authentication.getName());
        return ResponseEntity.ok(attendanceRepository.findByEmployeeId(employee.getId()));
    }
}
