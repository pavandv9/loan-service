package com.lending.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class EmiCalculationService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    /**
     * Calculates the EMI using the standard formula:
     * EMI = P * r * (1+r)^n / ((1+r)^n - 1)
     *
     */
    public BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRatePercent, int tenureMonths) {
        // r = annual rate / 12 / 100
        BigDecimal monthlyRate = annualRatePercent
                .divide(MONTHS_IN_YEAR, 10, ROUNDING_MODE)
                .divide(HUNDRED, 10, ROUNDING_MODE);

        // (1 + r)^n — use MathContext for precision with pow
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowN = onePlusR.pow(tenureMonths, new MathContext(15, ROUNDING_MODE));

        // Numerator: P * r * (1+r)^n
        BigDecimal numerator = principal
                .multiply(monthlyRate)
                .multiply(onePlusRPowN);

        // Denominator: (1+r)^n - 1
        BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, SCALE, ROUNDING_MODE);
    }

    /**
     * Calculates total payable amount over the full tenure.
     * Total Payable = EMI * tenure months
     */
    public BigDecimal calculateTotalPayable(BigDecimal emi, int tenureMonths) {
        return emi.multiply(new BigDecimal(tenureMonths)).setScale(SCALE, ROUNDING_MODE);
    }
}
