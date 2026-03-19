package com.lending.service;

import com.lending.dto.LoanApplicationRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class EligibilityService {

    private static final int MIN_CREDIT_SCORE = 600;
    private static final int MAX_AGE_PLUS_TENURE_YEARS = 65;
    private static final BigDecimal EMI_TO_INCOME_RATIO_LIMIT = new BigDecimal("0.60");
    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");

    /**
     * Evaluates all eligibility rules and returns a list of rejection reasons.
     * An empty list means the applicant passes eligibility checks.
     *
     * Note: EMI check here uses 60% threshold (hard rejection rule).
     * The offer generation step applies the stricter 50% threshold for offer validity.
     */
    public List<String> evaluate(LoanApplicationRequest request, BigDecimal emi) {
        List<String> rejectionReasons = new ArrayList<>();

        if (isCreditScoreTooLow(request.getApplicant().getCreditScore())) {
            rejectionReasons.add("CREDIT_SCORE_TOO_LOW");
        }

        if (isAgeTenureLimitExceeded(request.getApplicant().getAge(), request.getLoan().getTenureMonths())) {
            rejectionReasons.add("AGE_TENURE_LIMIT_EXCEEDED");
        }

        if (isEmiExceedsSixtyPercent(emi, request.getApplicant().getMonthlyIncome())) {
            rejectionReasons.add("EMI_EXCEEDS_60_PERCENT");
        }

        return rejectionReasons;
    }

    /**
     * Checks the offer-level rule: EMI must not exceed 50% of monthly income.
     */
    public boolean isEmiExceedsFiftyPercent(BigDecimal emi, BigDecimal monthlyIncome) {
        BigDecimal fiftyPercent = monthlyIncome.multiply(new BigDecimal("0.50"));
        return emi.compareTo(fiftyPercent) > 0;
    }

    private boolean isCreditScoreTooLow(int creditScore) {
        return creditScore < MIN_CREDIT_SCORE;
    }

    private boolean isAgeTenureLimitExceeded(int age, int tenureMonths) {
        BigDecimal tenureYears = new BigDecimal(tenureMonths)
                .divide(MONTHS_IN_YEAR, 2, RoundingMode.HALF_UP);
        BigDecimal ageAtCompletion = new BigDecimal(age).add(tenureYears);
        return ageAtCompletion.compareTo(new BigDecimal(MAX_AGE_PLUS_TENURE_YEARS)) > 0;
    }

    private boolean isEmiExceedsSixtyPercent(BigDecimal emi, BigDecimal monthlyIncome) {
        BigDecimal sixtyPercent = monthlyIncome.multiply(EMI_TO_INCOME_RATIO_LIMIT);
        return emi.compareTo(sixtyPercent) > 0;
    }
}
