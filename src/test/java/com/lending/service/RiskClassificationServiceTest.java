package com.lending.service;

import com.lending.constants.RiskBand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskClassificationServiceTest {

    private RiskClassificationService riskClassificationService;

    @BeforeEach
    void setUp() {
        riskClassificationService = new RiskClassificationService();
    }

    @Test
    @DisplayName("Credit score 750 or above should be LOW risk")
    void classify_scoreAbove750_returnsLow() {
        assertThat(riskClassificationService.classify(750)).isEqualTo(RiskBand.LOW);
        assertThat(riskClassificationService.classify(800)).isEqualTo(RiskBand.LOW);
        assertThat(riskClassificationService.classify(900)).isEqualTo(RiskBand.LOW);
    }

    @Test
    @DisplayName("Credit score between 650-749 should be MEDIUM risk")
    void classify_scoreBetween650And749_returnsMedium() {
        assertThat(riskClassificationService.classify(650)).isEqualTo(RiskBand.MEDIUM);
        assertThat(riskClassificationService.classify(700)).isEqualTo(RiskBand.MEDIUM);
        assertThat(riskClassificationService.classify(749)).isEqualTo(RiskBand.MEDIUM);
    }

    @Test
    @DisplayName("Credit score between 600-649 should be HIGH risk")
    void classify_scoreBetween600And649_returnsHigh() {
        assertThat(riskClassificationService.classify(600)).isEqualTo(RiskBand.HIGH);
        assertThat(riskClassificationService.classify(625)).isEqualTo(RiskBand.HIGH);
        assertThat(riskClassificationService.classify(649)).isEqualTo(RiskBand.HIGH);
    }
}
