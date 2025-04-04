package com.example.telecom.spring.service;


import com.example.telecom.spring.repository.CallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository repository;

    public List<CallRepository.YearlyCallSummary> getCallsPerYear(int startYear, int endYear) {
        return repository.findTotalCallsPerYear(startYear, endYear);
    }

    public List<CallRepository.MonthlyCallSummary> getCallsPerMonth(int year) {
        return repository.findTotalCallsPerMonth(year);
    }
}

