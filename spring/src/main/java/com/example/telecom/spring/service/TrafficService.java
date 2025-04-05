package com.example.telecom.spring.service;

import com.example.telecom.spring.repository.SessionSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrafficService {
    private final SessionSummaryRepository sessionSummaryRepository;

//    private final SmsStatusRepository smsStatusRepository;
//
//    private final SmsDayDistributionRepository smsDayDistributionRepository;

    public List<SessionSummaryRepository.YearlyTrafficSummary> getTrafficPerYear(int startYear, int endYear) {
        return sessionSummaryRepository.findTotalTrafficPerYear(startYear, endYear);
    }

    public List<SessionSummaryRepository.MonthlyTrafficSummary> getTrafficPerMonth(int year) {
        return sessionSummaryRepository.findTotalTrafficPerMonth(year);
    }

//    public List<SmsStatusRepository.SmsStatus> getStatusByYearMonth(int year, int month) {
//        return smsStatusRepository.findSmsStatusByYearAndMonth(year, month);
//    }
//
//    public List<SmsDayDistributionRepository.SmsDayDistribution> getSmsDayDistributionByMonth(int year, int month) {
//        return smsDayDistributionRepository.findSmsDayDistributionByMonth(year, month);
//    }
}

