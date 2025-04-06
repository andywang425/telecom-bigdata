package com.example.telecom.spring.model.vo;

import lombok.Data;

@Data
public class YearlyFailureRateVO {
    private Integer year;
    private Double callFailureRate;
    private Double smsFailureRate;
}
