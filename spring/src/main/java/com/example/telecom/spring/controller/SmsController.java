package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.ResponseUtils;
import com.example.telecom.spring.repository.SmsDayDistributionRepository;
import com.example.telecom.spring.repository.SmsStatusRepository;
import com.example.telecom.spring.repository.SmsSummaryRepository;
import com.example.telecom.spring.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {
    private final SmsService service;

    @GetMapping("/summary/yearly")
    public BaseResponse<List<SmsSummaryRepository.YearlySmsSummary>> getYearlyCalls(
            @RequestParam int startYear,
            @RequestParam int endYear) {

        List<SmsSummaryRepository.YearlySmsSummary> results = service.getCallsPerYear(startYear, endYear);
        return ResponseUtils.success(results);
    }

    @GetMapping("/summary/monthly")
    public BaseResponse<List<SmsSummaryRepository.MonthlySmsSummary>> getMonthlyCalls(
            @RequestParam int year) {

        List<SmsSummaryRepository.MonthlySmsSummary> results = service.getCallsPerMonth(year);
        return ResponseUtils.success(results);
    }

    @GetMapping("/status")
    public BaseResponse<List<SmsStatusRepository.SmsStatus>> getSmsStatus(
            @RequestParam int year, @RequestParam int month) {

        List<SmsStatusRepository.SmsStatus> results = service.getStatusByYearMonth(year, month);
        return ResponseUtils.success(results);
    }

    @GetMapping("/distribution")
    public BaseResponse<List<SmsDayDistributionRepository.SmsDayDistribution>> getSmsDayDistribution(
            @RequestParam int year, @RequestParam int month) {

        List<SmsDayDistributionRepository.SmsDayDistribution> results = service.getSmsDayDistributionByMonth(year, month);
        return ResponseUtils.success(results);
    }
}

