package com.hrms.service.impl;

import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Employee;
import com.hrms.repository.EmployeeRepository;
import com.hrms.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployeeByHr(Long id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));

        employee.setName(employeeDetails.getName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setRole(employeeDetails.getRole());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setJoiningDate(employeeDetails.getJoiningDate());
        
        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));
        employeeRepository.delete(employee);
    }

    @Override
    public Employee getMyProfile(String email) {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found for email :: " + email);
        }
        return employee;
    }

    @Override
    public Employee updateMyProfile(String email, Employee updates) {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found for email :: " + email);
        }
        employee.setPhone(updates.getPhone());
        employee.setAddress(updates.getAddress());
        return employeeRepository.save(employee);
    }
}
