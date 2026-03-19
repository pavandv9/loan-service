package com.lending.responsedto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OfferResponse {

    private BigDecimal interestRate;
    private int tenureMonths;
    private BigDecimal emi;
    private BigDecimal totalPayable;
}
