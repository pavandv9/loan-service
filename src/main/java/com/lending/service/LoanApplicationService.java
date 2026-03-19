package com.lending.service;

import com.lending.dto.LoanApplicationRequest;
import com.lending.repository.LoanApplicationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository repository;

    public String process(LoanApplicationRequest request) {
        return null;
    }
}
