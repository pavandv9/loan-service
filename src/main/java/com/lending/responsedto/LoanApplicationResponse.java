package com.lending.responsedto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lending.utils.ApplicationStatus;
import com.lending.utils.RiskBand;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class LoanApplicationResponse {

    private UUID applicationId;
    private ApplicationStatus status;
    private RiskBand riskBand;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OfferResponse offer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> rejectionReasons;
}
