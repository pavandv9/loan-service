package com.lending.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EmiCalculationServiceTest {

    private EmiCalculationService emiCalculationService;

    @BeforeEach
    void setUp() {
        emiCalculationService = new EmiCalculationService();
    }


    @Test
    @DisplayName("EMI calculation - longer tenure reduces EMI")
    void calculateEmi_longerTenureProducesLowerEmi() {
        BigDecimal principal = new BigDecimal("500000");
        BigDecimal annualRate = new BigDecimal("12.0");

        BigDecimal emiShort = emiCalculationService.calculateEmi(principal, annualRate, 12);
        BigDecimal emiMed = emiCalculationService.calculateEmi(principal, annualRate, 36);
        BigDecimal emiLong = emiCalculationService.calculateEmi(principal, annualRate, 60);

        assertThat(emiMed).isGreaterThan(emiLong);
        assertThat(emiShort).isGreaterThan(emiLong);
    }

}
