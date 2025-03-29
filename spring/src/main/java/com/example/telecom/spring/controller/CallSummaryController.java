package com.example.telecom.spring.controller;
import com.example.telecom.spring.repository.MonthlyCallSummaryRepository;
import com.example.telecom.spring.service.CallSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallSummaryController {

    private final CallSummaryService service;

    @GetMapping("/yearly")
    public ResponseEntity<List<MonthlyCallSummaryRepository.YearlyCallSummary>> getYearlyCalls(
            @RequestParam int startYear,
            @RequestParam int endYear) {

        List<MonthlyCallSummaryRepository.YearlyCallSummary> results = service.getCallsPerYear(startYear, endYear);
        return ResponseEntity.ok(results);
    }
}

