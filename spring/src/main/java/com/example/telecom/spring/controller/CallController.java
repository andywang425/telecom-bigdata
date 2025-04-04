package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.ResponseUtils;
import com.example.telecom.spring.repository.CallStatusRepository;
import com.example.telecom.spring.repository.CallSummaryRepository;
import com.example.telecom.spring.service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/call")
@RequiredArgsConstructor
public class CallController {

    private final CallService service;

    @GetMapping("/summary/yearly")
    public BaseResponse<List<CallSummaryRepository.YearlyCallSummary>> getYearlyCalls(
            @RequestParam int startYear,
            @RequestParam int endYear) {

        List<CallSummaryRepository.YearlyCallSummary> results = service.getCallsPerYear(startYear, endYear);
        return ResponseUtils.success(results);
    }

    @GetMapping("/summary/monthly")
    public BaseResponse<List<CallSummaryRepository.MonthlyCallSummary>> getMonthlyCalls(
            @RequestParam int year) {

        List<CallSummaryRepository.MonthlyCallSummary> results = service.getCallsPerMonth(year);
        return ResponseUtils.success(results);
    }

    @GetMapping("/status")
    public BaseResponse<List<CallStatusRepository.CallStatus>> getCallStatus(
            @RequestParam int year, @RequestParam int month) {

        List<CallStatusRepository.CallStatus> results = service.getStatusByYearMonth(year, month);
        return ResponseUtils.success(results);
    }
}

