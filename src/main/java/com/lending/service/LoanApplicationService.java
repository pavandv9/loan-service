package com.lending.service;

import com.lending.dto.LoanApplicationRequest;
import com.lending.entity.LoanApplication;
import com.lending.repository.LoanApplicationRepository;
import com.lending.responsedto.LoanApplicationResponse;
import com.lending.responsedto.OfferResponse;
import com.lending.constants.ApplicationStatus;
import com.lending.constants.RiskBand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository repository;
    private final RiskClassificationService riskClassificationService;
    private final InterestRateService interestRateService;
    private final EmiCalculationService emiCalculationService;
    private final EligibilityService eligibilityService;

    public LoanApplicationResponse process(LoanApplicationRequest request) {
        int creditScore = request.getApplicant().getCreditScore();
        BigDecimal loanAmount = request.getLoan().getAmount();
        int tenureMonths = request.getLoan().getTenureMonths();
        BigDecimal monthlyIncome = request.getApplicant().getMonthlyIncome();

        RiskBand riskBand = riskClassificationService.classify(creditScore);

        BigDecimal interestRate = interestRateService.calculate(
                riskBand,
                request.getApplicant().getEmploymentType(),
                loanAmount
        );

        BigDecimal emi = emiCalculationService.calculateEmi(loanAmount, interestRate, tenureMonths);

        List<String> rejectionReasons = eligibilityService.evaluate(request, emi);

        // Also apply the 50% offer-level rule if not already rejected
        if (rejectionReasons.isEmpty() && eligibilityService.isEmiExceedsFiftyPercent(emi, monthlyIncome)) {
            rejectionReasons.add("EMI_EXCEEDS_50_PERCENT_OF_INCOME");
        }

        if (!rejectionReasons.isEmpty()) {
            LoanApplication rejected = buildRejectedApplication(request, rejectionReasons);
            LoanApplication saved = repository.save(rejected);
            return buildRejectedResponse(saved, rejectionReasons);
        }

        BigDecimal totalPayable = emiCalculationService.calculateTotalPayable(emi, tenureMonths);

        LoanApplication approved = buildApprovedApplication(request, riskBand, interestRate, emi, totalPayable);
        LoanApplication saved = repository.save(approved);
        return buildApprovedResponse(saved, interestRate, tenureMonths, emi, totalPayable);
    }

    private LoanApplication buildApprovedApplication(
            LoanApplicationRequest request,
            RiskBand riskBand,
            BigDecimal interestRate,
            BigDecimal emi,
            BigDecimal totalPayable) {

        return LoanApplication.builder()
                .applicantName(request.getApplicant().getName())
                .age(request.getApplicant().getAge())
                .monthlyIncome(request.getApplicant().getMonthlyIncome())
                .employmentType(request.getApplicant().getEmploymentType())
                .creditScore(request.getApplicant().getCreditScore())
                .loanAmount(request.getLoan().getAmount())
                .tenureMonths(request.getLoan().getTenureMonths())
                .loanPurpose(request.getLoan().getPurpose())
                .status(ApplicationStatus.APPROVED)
                .riskBand(riskBand)
                .interestRate(interestRate)
                .emi(emi)
                .totalPayable(totalPayable)
                .build();
    }

    private LoanApplication buildRejectedApplication(
            LoanApplicationRequest request,
            List<String> rejectionReasons) {

        return LoanApplication.builder()
                .applicantName(request.getApplicant().getName())
                .age(request.getApplicant().getAge())
                .monthlyIncome(request.getApplicant().getMonthlyIncome())
                .employmentType(request.getApplicant().getEmploymentType())
                .creditScore(request.getApplicant().getCreditScore())
                .loanAmount(request.getLoan().getAmount())
                .tenureMonths(request.getLoan().getTenureMonths())
                .loanPurpose(request.getLoan().getPurpose())
                .status(ApplicationStatus.REJECTED)
                .riskBand(null)
                .rejectionReasons(String.join(",", rejectionReasons))
                .build();
    }

    private LoanApplicationResponse buildApprovedResponse(
            LoanApplication saved,
            BigDecimal interestRate,
            int tenureMonths,
            BigDecimal emi,
            BigDecimal totalPayable) {

        OfferResponse offer = OfferResponse.builder()
                .interestRate(interestRate)
                .tenureMonths(tenureMonths)
                .emi(emi)
                .totalPayable(totalPayable)
                .build();

        return LoanApplicationResponse.builder()
                .applicationId(saved.getId())
                .status(ApplicationStatus.APPROVED)
                .riskBand(saved.getRiskBand())
                .offer(offer)
                .build();
    }

    private LoanApplicationResponse buildRejectedResponse(
            LoanApplication saved,
            List<String> rejectionReasons) {

        return LoanApplicationResponse.builder()
                .applicationId(saved.getId())
                .status(ApplicationStatus.REJECTED)
                .riskBand(null)
                .rejectionReasons(rejectionReasons)
                .build();
    }
}
