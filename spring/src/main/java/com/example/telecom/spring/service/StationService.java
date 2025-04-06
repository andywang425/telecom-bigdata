package com.example.telecom.spring.service;

import com.example.telecom.spring.model.entity.BaseStation;
import com.example.telecom.spring.model.vo.MonthlyFailureRateVO;
import com.example.telecom.spring.model.vo.YearlyFailureRateVO;
import com.example.telecom.spring.repository.BaseStationRepository;
import com.example.telecom.spring.repository.StationCallFailureRateRepository;
import com.example.telecom.spring.repository.StationSmsFailureRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StationService {
    private final BaseStationRepository baseStationRepository;

    private final StationCallFailureRateRepository stationCallFailureRateRepository;

    private final StationSmsFailureRateRepository stationSmsFailureRateRepository;

    public List<BaseStation> getAll() {
        return baseStationRepository.findAll();
    }

    public List<YearlyFailureRateVO> getFailureRatePerYear(int startYear, int endYear) {
        List<StationCallFailureRateRepository.YearlyCallFailureRate> callFailureRates = stationCallFailureRateRepository.findFailureRatePerYear(startYear, endYear);

        List<StationSmsFailureRateRepository.YearlySmsFailureRate> smsFailureRates = stationSmsFailureRateRepository.findFailureRatePerYear(startYear, endYear);

        // 将数据转换为Map，以便快速查找
        Map<Integer, StationCallFailureRateRepository.YearlyCallFailureRate> callFailureRateMap = new HashMap<>();
        for (StationCallFailureRateRepository.YearlyCallFailureRate rate : callFailureRates) {
            callFailureRateMap.put(rate.getYear(), rate);
        }

        Map<Integer, StationSmsFailureRateRepository.YearlySmsFailureRate> smsFailureRateMap = new HashMap<>();
        for (StationSmsFailureRateRepository.YearlySmsFailureRate rate : smsFailureRates) {
            smsFailureRateMap.put(rate.getYear(), rate);
        }

        // 构建结果列表
        List<YearlyFailureRateVO> failureRateVOS = new ArrayList<>();

        for (int year = startYear; year <= endYear; year++) {
            StationCallFailureRateRepository.YearlyCallFailureRate callRate = callFailureRateMap.get(year);
            StationSmsFailureRateRepository.YearlySmsFailureRate smsRate = smsFailureRateMap.get(year);

            // 如果某一年的数据缺失，跳过该年份
            if (callRate != null && smsRate != null) {
                YearlyFailureRateVO failureRateVO = new YearlyFailureRateVO();
                failureRateVO.setYear(year);
                failureRateVO.setCallFailureRate(callRate.getCallFailureRate());
                failureRateVO.setSmsFailureRate(smsRate.getSmsFailureRate());
                failureRateVOS.add(failureRateVO);
            }
        }

        return failureRateVOS;
    }

    public List<MonthlyFailureRateVO> getFailureRatePerMonth(int year) {
        List<StationCallFailureRateRepository.MonthlyCallFailureRate> callFailureRates = stationCallFailureRateRepository.findFailureRatePerMonth(year);

        List<StationSmsFailureRateRepository.MonthlySmsFailureRate> smsFailureRates = stationSmsFailureRateRepository.findFailureRatePerMonth(year);

        // 将数据转换为Map，以便快速查找
        Map<Integer, StationCallFailureRateRepository.MonthlyCallFailureRate> callFailureRateMap = new HashMap<>();
        for (StationCallFailureRateRepository.MonthlyCallFailureRate rate : callFailureRates) {
            callFailureRateMap.put(rate.getMonth(), rate);
        }

        Map<Integer, StationSmsFailureRateRepository.MonthlySmsFailureRate> smsFailureRateMap = new HashMap<>();
        for (StationSmsFailureRateRepository.MonthlySmsFailureRate rate : smsFailureRates) {
            smsFailureRateMap.put(rate.getMonth(), rate);
        }

        // 构建结果列表
        List<MonthlyFailureRateVO> failureRateVOS = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            StationCallFailureRateRepository.MonthlyCallFailureRate callRate = callFailureRateMap.get(month);
            StationSmsFailureRateRepository.MonthlySmsFailureRate smsRate = smsFailureRateMap.get(month);

            // 如果某月的数据缺失，跳过该月
            if (callRate != null && smsRate != null) {
                MonthlyFailureRateVO failureRateVO = new MonthlyFailureRateVO();
                failureRateVO.setMonth(month);
                failureRateVO.setCallFailureRate(callRate.getCallFailureRate());
                failureRateVO.setSmsFailureRate(smsRate.getSmsFailureRate());
                failureRateVOS.add(failureRateVO);
            }
        }

        return failureRateVOS;
    }
}

