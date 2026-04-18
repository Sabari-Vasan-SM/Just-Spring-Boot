package com.hrms.controller;

import com.hrms.model.Employee;
import com.hrms.repository.EmployeeRepository;
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
    private EmployeeRepository employeeRepository;

    // HR Actions
    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Employee> updateEmployeeByHr(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        employee.setName(employeeDetails.getName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setRole(employeeDetails.getRole());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setJoiningDate(employeeDetails.getJoiningDate());
        return ResponseEntity.ok(employeeRepository.save(employee));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Employee Actions
    @GetMapping("/me")
    public ResponseEntity<Employee> getMyProfile(Authentication authentication) {
        Employee employee = employeeRepository.findByEmail(authentication.getName());
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/me")
    public ResponseEntity<Employee> updateMyProfile(Authentication authentication, @RequestBody Employee updates) {
        Employee employee = employeeRepository.findByEmail(authentication.getName());
        employee.setPhone(updates.getPhone());
        employee.setAddress(updates.getAddress());
        return ResponseEntity.ok(employeeRepository.save(employee));
    }
}