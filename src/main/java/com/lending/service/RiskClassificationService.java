package com.lending.service;

import com.lending.utils.RiskBand;
import org.springframework.stereotype.Service;

@Service
public class RiskClassificationService {

    /**
     * Classifies applicant risk based on credit score.
     * 750+  -> LOW
     * 650-749 -> MEDIUM
     * 600-649 -> HIGH
     */
    public RiskBand classify(int creditScore) {
        if (creditScore >= 750) {
            return RiskBand.LOW;
        } else if (creditScore >= 650) {
            return RiskBand.MEDIUM;
        } else {
            return RiskBand.HIGH;
        }
    }
}
