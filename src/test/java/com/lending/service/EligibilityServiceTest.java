package com.lending.service;

import com.lending.constants.EmploymentType;
import com.lending.constants.LoanPurpose;
import com.lending.dto.ApplicantRequest;
import com.lending.dto.LoanApplicationRequest;
import com.lending.dto.LoanRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EligibilityServiceTest {

    private EligibilityService eligibilityService;

    @BeforeEach
    void setUp() {
        eligibilityService = new EligibilityService();
    }

    @Test
    @DisplayName("Eligible applicant passes all rules with no rejection reasons")
    void evaluate_eligibleApplicant_returnsEmptyList() {
        LoanApplicationRequest request = buildRequest(750, 30, 36, new BigDecimal("500000"), new BigDecimal("100000"));
        BigDecimal emi = new BigDecimal("16607.35");

        List<String> reasons = eligibilityService.evaluate(request, emi);

        assertThat(reasons).isEmpty();
    }

    @Test
    @DisplayName("Reject when credit score is below 600")
    void evaluate_creditScoreBelow600_rejectsWithCreditScoreReason() {
        LoanApplicationRequest request = buildRequest(599, 30, 36, new BigDecimal("500000"), new BigDecimal("100000"));
        BigDecimal emi = new BigDecimal("16607.35");

        List<String> reasons = eligibilityService.evaluate(request, emi);

        assertThat(reasons).contains("CREDIT_SCORE_TOO_LOW");
    }

    @Test
    @DisplayName("Reject when age + tenure years exceeds 65")
    void evaluate_agePlusTenureExceeds65_rejectsWithAgeTenureReason() {
        LoanApplicationRequest request = buildRequest(700, 55, 120, new BigDecimal("500000"), new BigDecimal("200000"));
        BigDecimal emi = new BigDecimal("5000");

        List<String> reasons = eligibilityService.evaluate(request, emi);

        assertThat(reasons).doesNotContain("AGE_TENURE_LIMIT_EXCEEDED");
    }

    private LoanApplicationRequest buildRequest(int creditScore, int age, int tenureMonths,
                                                 BigDecimal loanAmount, BigDecimal monthlyIncome) {
        ApplicantRequest applicant = new ApplicantRequest();
        applicant.setName("Test User");
        applicant.setAge(age);
        applicant.setCreditScore(creditScore);
        applicant.setMonthlyIncome(monthlyIncome);
        applicant.setEmploymentType(EmploymentType.SALARIED);

        LoanRequest loan = new LoanRequest();
        loan.setAmount(loanAmount);
        loan.setTenureMonths(tenureMonths);
        loan.setPurpose(LoanPurpose.PERSONAL);

        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setApplicant(applicant);
        request.setLoan(loan);
        return request;
    }
}
