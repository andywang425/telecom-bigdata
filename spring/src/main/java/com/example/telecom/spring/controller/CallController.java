package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.ResultUtils;
import com.example.telecom.spring.repository.CallRepository;
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
    public BaseResponse<List<CallRepository.YearlyCallSummary>> getYearlyCalls(
            @RequestParam int startYear,
            @RequestParam int endYear) {

        List<CallRepository.YearlyCallSummary> results = service.getCallsPerYear(startYear, endYear);
        return ResultUtils.success(results);
    }
}

