package com.example.telecom.spring.service;

import com.example.telecom.spring.repository.SmsDayDistributionRepository;
import com.example.telecom.spring.repository.SmsStatusRepository;
import com.example.telecom.spring.repository.SmsSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsService {
    private final SmsSummaryRepository smsSummaryRepository;

    private final SmsStatusRepository smsStatusRepository;

    private final SmsDayDistributionRepository smsDayDistributionRepository;

    public List<SmsSummaryRepository.YearlySmsSummary> getCallsPerYear(int startYear, int endYear) {
        return smsSummaryRepository.findTotalSmsPerYear(startYear, endYear);
    }

    public List<SmsSummaryRepository.MonthlySmsSummary> getCallsPerMonth(int year) {
        return smsSummaryRepository.findTotalSmsPerMonth(year);
    }

    public List<SmsStatusRepository.SmsStatus> getStatusByYearMonth(int year, int month) {
        return smsStatusRepository.findSmsStatusByYearAndMonth(year, month);
    }

    public List<SmsDayDistributionRepository.SmsDayDistribution> getSmsDayDistributionByMonth(int year, int month) {
        return smsDayDistributionRepository.findSmsDayDistributionByMonth(year, month);
    }
}

