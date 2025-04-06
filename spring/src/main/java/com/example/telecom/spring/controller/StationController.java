package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.ResponseUtils;
import com.example.telecom.spring.model.entity.BaseStation;
import com.example.telecom.spring.model.vo.MonthlyFailureRateVO;
import com.example.telecom.spring.model.vo.YearlyFailureRateVO;
import com.example.telecom.spring.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/station")
@RequiredArgsConstructor
public class StationController {
    private final StationService service;

    @GetMapping("/all")
    public BaseResponse<List<BaseStation>> getAll() {
        List<BaseStation> results = service.getAll();
        return ResponseUtils.success(results);
    }

    @GetMapping("/failure/yearly")
    public BaseResponse<List<YearlyFailureRateVO>> getYearlyCalls(
            @RequestParam int startYear,
            @RequestParam int endYear) {

        List<YearlyFailureRateVO> results = service.getFailureRatePerYear(startYear, endYear);
        return ResponseUtils.success(results);
    }

    @GetMapping("/failure/monthly")
    public BaseResponse<List<MonthlyFailureRateVO>> getMonthlyCalls(
            @RequestParam int year) {

        List<MonthlyFailureRateVO> results = service.getFailureRatePerMonth(year);
        return ResponseUtils.success(results);
    }
}

