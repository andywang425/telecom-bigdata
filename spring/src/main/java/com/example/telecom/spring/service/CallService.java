package com.example.telecom.spring.service;

import com.example.telecom.spring.repository.CallStatusRepository;
import com.example.telecom.spring.repository.CallSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallSummaryRepository callSummaryRepository;

    private final CallStatusRepository callStatusRepository;

    public List<CallSummaryRepository.YearlyCallSummary> getCallsPerYear(int startYear, int endYear) {
        return callSummaryRepository.findTotalCallsPerYear(startYear, endYear);
    }

    public List<CallSummaryRepository.MonthlyCallSummary> getCallsPerMonth(int year) {
        return callSummaryRepository.findTotalCallsPerMonth(year);
    }

    public List<CallStatusRepository.CallStatus> getStatusByYearMonth(int year, int month) {
        return callStatusRepository.findCallStatusByYearAndMonth(year, month);
    }
}

