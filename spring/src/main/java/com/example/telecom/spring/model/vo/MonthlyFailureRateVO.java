package com.example.telecom.spring.model.vo;

import lombok.Data;

@Data
public class MonthlyFailureRateVO {
    private Integer month;
    private Double callFailureRate;
    private Double smsFailureRate;
}
