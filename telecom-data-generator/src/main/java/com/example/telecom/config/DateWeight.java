package com.example.telecom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateWeight {
    private Map<String, Double> year;
    private Map<String, Double> month;
}
