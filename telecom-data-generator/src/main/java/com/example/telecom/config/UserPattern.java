package com.example.telecom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPattern {
    private String name;
    private double weight;
    private CallDetails call;
    private SmsDetails sms;
    private TrafficDetails traffic;
}
