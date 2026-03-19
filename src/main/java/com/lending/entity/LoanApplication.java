package com.lending.entity;

import com.lending.constants.ApplicationStatus;
import com.lending.constants.EmploymentType;
import com.lending.constants.LoanPurpose;
import com.lending.constants.RiskBand;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "LOAN_APPLICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Applicant details
    private String applicantName;
    private int age;
    private BigDecimal monthlyIncome;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    private int creditScore;

    // Loan details
    private BigDecimal loanAmount;
    private int tenureMonths;

    @Enumerated(EnumType.STRING)
    private LoanPurpose loanPurpose;

    // Decision
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    private RiskBand riskBand;

    // Offer details (populated on approval)
    private BigDecimal interestRate;
    private BigDecimal emi;
    private BigDecimal totalPayable;

    // Rejection reasons stored as comma-separated string
    private String rejectionReasons;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
