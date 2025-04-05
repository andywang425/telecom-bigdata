package com.example.telecom.spring.service;

import com.example.telecom.spring.repository.SessionSummaryRepository;
import com.example.telecom.spring.repository.SessionTrafficByAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrafficService {
    private final SessionSummaryRepository sessionSummaryRepository;

    private final SessionTrafficByAppRepository sessionTrafficByAppRepository;
//
//    private final SmsDayDistributionRepository smsDayDistributionRepository;

    public List<SessionSummaryRepository.YearlyTrafficSummary> getTrafficPerYear(int startYear, int endYear) {
        return sessionSummaryRepository.findTotalTrafficPerYear(startYear, endYear);
    }

    public List<SessionSummaryRepository.MonthlyTrafficSummary> getTrafficPerMonth(int year) {
        return sessionSummaryRepository.findTotalTrafficPerMonth(year);
    }

    public List<SessionTrafficByAppRepository.TrafficApplicationType> getApplicationTypeByYearMonth(int year, int month) {
        return sessionTrafficByAppRepository.findByYearMonth(year, month);
    }
//
//    public List<SmsDayDistributionRepository.SmsDayDistribution> getSmsDayDistributionByMonth(int year, int month) {
//        return smsDayDistributionRepository.findSmsDayDistributionByMonth(year, month);
//    }
}

