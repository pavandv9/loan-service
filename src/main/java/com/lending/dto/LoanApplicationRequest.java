package com.lending.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanApplicationRequest {

    @NotNull(message = "Applicant details are required")
    @Valid
    private ApplicantRequest applicant;

    @NotNull(message = "Loan details are required")
    @Valid
    private LoanRequest loan;
}
