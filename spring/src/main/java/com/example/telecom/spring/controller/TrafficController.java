package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.ResponseUtils;
import com.example.telecom.spring.repository.SessionSummaryRepository;
import com.example.telecom.spring.service.TrafficService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
@RequiredArgsConstructor
public class TrafficController {
    private final TrafficService service;

    @GetMapping("/summary/yearly")
    public BaseResponse<List<SessionSummaryRepository.YearlyTrafficSummary>> getYearlyTraffic(
            @RequestParam int startYear,
            @RequestParam int endYear) {

        List<SessionSummaryRepository.YearlyTrafficSummary> results = service.getTrafficPerYear(startYear, endYear);
        return ResponseUtils.success(results);
    }

    @GetMapping("/summary/monthly")
    public BaseResponse<List<SessionSummaryRepository.MonthlyTrafficSummary>> getMonthlyTraffic(
            @RequestParam int year) {

        List<SessionSummaryRepository.MonthlyTrafficSummary> results = service.getTrafficPerMonth(year);
        return ResponseUtils.success(results);
    }

//    @GetMapping("/status")
//    public BaseResponse<List<SmsStatusRepository.SmsStatus>> getSmsStatus(
//            @RequestParam int year, @RequestParam int month) {
//
//        List<SmsStatusRepository.SmsStatus> results = service.getStatusByYearMonth(year, month);
//        return ResponseUtils.success(results);
//    }
//
//    @GetMapping("/distribution")
//    public BaseResponse<List<SmsDayDistributionRepository.SmsDayDistribution>> getSmsDayDistribution(
//            @RequestParam int year, @RequestParam int month) {
//
//        List<SmsDayDistributionRepository.SmsDayDistribution> results = service.getSmsDayDistributionByMonth(year, month);
//        return ResponseUtils.success(results);
//    }
}

