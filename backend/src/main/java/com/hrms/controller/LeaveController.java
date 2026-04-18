package com.hrms.controller;

import com.hrms.model.Employee;
import com.hrms.model.LeaveRequest;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = "*")
public class LeaveController {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/apply")
    public ResponseEntity<LeaveRequest> applyForLeave(Authentication authentication, @RequestBody LeaveRequest leaveRequest) {
        Employee employee = employeeRepository.findByEmail(authentication.getName());
        
        leaveRequest.setEmployee(employee);
        leaveRequest.setStatus("PENDING");
        
        return ResponseEntity.ok(leaveRequestRepository.save(leaveRequest));
    }

    @GetMapping("/my-leaves")
    public ResponseEntity<List<LeaveRequest>> getMyLeaves(Authentication authentication) {
        Employee employee = employeeRepository.findByEmail(authentication.getName());
        return ResponseEntity.ok(leaveRequestRepository.findByEmployeeId(employee.getId()));
    }

    // HR Operations
    @GetMapping("/all")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {
        return ResponseEntity.ok(leaveRequestRepository.findAll());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<LeaveRequest> updateLeaveStatus(@PathVariable Long id, @RequestParam String status) {
        LeaveRequest request = leaveRequestRepository.findById(id).orElseThrow();
        request.setStatus(status.toUpperCase());
        return ResponseEntity.ok(leaveRequestRepository.save(request));
    }
}
