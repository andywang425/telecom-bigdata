package com.example.telecom.spring.service;


import com.example.telecom.spring.repository.MonthlyCallSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CallSummaryService {

    private final MonthlyCallSummaryRepository repository;

    public List<MonthlyCallSummaryRepository.YearlyCallSummary> getCallsPerYear(int startYear, int endYear) {
        return repository.findTotalCallsPerYear(startYear, endYear);
    }
}

