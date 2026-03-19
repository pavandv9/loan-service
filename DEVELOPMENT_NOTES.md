# Development Notes

## Overall Approach

The development followed this sequence:
1. Dependency setup
2. Entity and DTOs
3. Request/response DTOs
4. Repository layer
5. Business logic services
7. Controller and global exception handling
8. Unit tests

---

## Key Design Decisions

Rather than placing all logic in one large service, each responsibility has its own class:
1. RiskClassificationService - credit score - risk band
2. InterestRateService - rate calculation from premiums
3. EmiCalculationService - EMI and total payable math
4. EligibilityService - rejection rule evaluation
5. LoanApplicationService - persists, and shapes the response

This makes unit testing each rule set trivial and keeps classes small.

---

## Assumptions Made

1. Age + tenure check uses decimal years - e.g., 55 years + 120 months = 55 + 10.00 years = 65.00, which is treated as not exceeding the limit (boundary is exclusive: strictly > 65 is rejected).
2. riskBand is null on rejection - the spec shows "riskBand": null in the rejected response.
3. creditScore < 600 is rejected before risk classification - applicants with scores below 600 do not map to any risk band
5. Monthly income > 0 validated via @DecimalMin("0.01")** - covers the spec requirement that income must be greater than 0.

---

## Improvements with More Time

1. GET /applications/{id} - Allow clients to retrieve a previously submitted application by ID.
2. Rejection reason normalisation - Store rejection reasons in a child table(LOAN_REJECTION_REASON).
