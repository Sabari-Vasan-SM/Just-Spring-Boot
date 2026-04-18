package com.hrms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private LocalDate startDate;
    
    private LocalDate endDate;
    
    // e.g., SICK_LEAVE, CASUAL_LEAVE, ANNUAL_LEAVE
    private String leaveType;

    private String reason;
    
    // PENDING, APPROVED, REJECTED
    private String status;
}
