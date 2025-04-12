package com.example.telecom.spring.service;

import com.example.telecom.spring.repository.TrafficSummaryRepository;
import com.example.telecom.spring.repository.TrafficByAppRepository;
import com.example.telecom.spring.repository.TrafficByNetworkTechnologyRepository;
import com.example.telecom.spring.repository.TrafficDayDistributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrafficService {
    private final TrafficSummaryRepository trafficSummaryRepository;

    private final TrafficByAppRepository trafficByAppRepository;

    private final TrafficByNetworkTechnologyRepository trafficByNetworkTechnologyRepository;

    private final TrafficDayDistributionRepository trafficDayDistributionRepository;

    public List<TrafficSummaryRepository.YearlyTrafficSummary> getTrafficPerYear(int startYear, int endYear) {
        return trafficSummaryRepository.findTotalTrafficPerYear(startYear, endYear);
    }

    public List<TrafficSummaryRepository.MonthlyTrafficSummary> getTrafficPerMonth(int year) {
        return trafficSummaryRepository.findTotalTrafficPerMonth(year);
    }

    public List<TrafficByAppRepository.TrafficApplicationType> getApplicationTypeByYearMonth(int year, int month) {
        return trafficByAppRepository.findByYearMonth(year, month);
    }

    public List<TrafficByNetworkTechnologyRepository.TrafficNetworkTechnology> getNetworkTechnologyByYearMonth(int year, int month) {
        return trafficByNetworkTechnologyRepository.findByYearMonth(year, month);
    }

    public List<TrafficDayDistributionRepository.TrafficDayDistribution> getTrafficDayDistributionByMonth(int year, int month) {
        return trafficDayDistributionRepository.findTrafficDayDistributionByMonth(year, month);
    }
}

