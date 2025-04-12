package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.ResponseUtils;
import com.example.telecom.spring.repository.TrafficSummaryRepository;
import com.example.telecom.spring.repository.TrafficByAppRepository;
import com.example.telecom.spring.repository.TrafficByNetworkTechnologyRepository;
import com.example.telecom.spring.repository.TrafficDayDistributionRepository;
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
    public BaseResponse<List<TrafficSummaryRepository.YearlyTrafficSummary>> getYearlyTraffic(
            @RequestParam int startYear,
            @RequestParam int endYear) {

        List<TrafficSummaryRepository.YearlyTrafficSummary> results = service.getTrafficPerYear(startYear, endYear);
        return ResponseUtils.success(results);
    }

    @GetMapping("/summary/monthly")
    public BaseResponse<List<TrafficSummaryRepository.MonthlyTrafficSummary>> getMonthlyTraffic(
            @RequestParam int year) {

        List<TrafficSummaryRepository.MonthlyTrafficSummary> results = service.getTrafficPerMonth(year);
        return ResponseUtils.success(results);
    }

    @GetMapping("/app")
    public BaseResponse<List<TrafficByAppRepository.TrafficApplicationType>> getTrafficApplicationType(
            @RequestParam int year, @RequestParam int month) {

        List<TrafficByAppRepository.TrafficApplicationType> results = service.getApplicationTypeByYearMonth(year, month);
        return ResponseUtils.success(results);
    }

    @GetMapping("/technology")
    public BaseResponse<List<TrafficByNetworkTechnologyRepository.TrafficNetworkTechnology>> getTrafficNetworkTechnology(
            @RequestParam int year, @RequestParam int month) {

        List<TrafficByNetworkTechnologyRepository.TrafficNetworkTechnology> results = service.getNetworkTechnologyByYearMonth(year, month);
        return ResponseUtils.success(results);
    }

    @GetMapping("/distribution")
    public BaseResponse<List<TrafficDayDistributionRepository.TrafficDayDistribution>> getTrafficDayDistribution(
            @RequestParam int year, @RequestParam int month) {

        List<TrafficDayDistributionRepository.TrafficDayDistribution> results = service.getTrafficDayDistributionByMonth(year, month);
        return ResponseUtils.success(results);
    }
}

