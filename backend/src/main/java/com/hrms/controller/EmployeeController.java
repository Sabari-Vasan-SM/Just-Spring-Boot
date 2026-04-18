package com.hrms.controller;

import com.hrms.model.Employee;
import com.hrms.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*") // Allow Vue.js frontend
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // HR Actions
    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Employee> updateEmployeeByHr(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        Employee updatedEmployee = employeeService.updateEmployeeByHr(id, employeeDetails);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

    // Employee Actions
    @GetMapping("/me")
    public ResponseEntity<Employee> getMyProfile(Authentication authentication) {
        Employee employee = employeeService.getMyProfile(authentication.getName());
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/me")
    public ResponseEntity<Employee> updateMyProfile(Authentication authentication, @RequestBody Employee updates) {
        Employee updatedEmployee = employeeService.updateMyProfile(authentication.getName(), updates);
        return ResponseEntity.ok(updatedEmployee);
    }
}