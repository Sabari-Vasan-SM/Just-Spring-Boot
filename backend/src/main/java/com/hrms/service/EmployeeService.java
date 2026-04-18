package com.hrms.service;

import com.hrms.model.Employee;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee addEmployee(Employee employee);
    Employee updateEmployeeByHr(Long id, Employee employeeDetails);
    void deleteEmployee(Long id);
    Employee getMyProfile(String email);
    Employee updateMyProfile(String email, Employee updates);
}
