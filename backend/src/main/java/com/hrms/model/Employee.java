package com.hrms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private String role; // HR, EMPLOYEE
    private String department;
    private LocalDate joiningDate;
    
    private String phone;
    private String address;
    private String password;
}