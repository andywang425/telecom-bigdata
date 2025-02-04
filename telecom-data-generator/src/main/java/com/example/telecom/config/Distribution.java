package com.example.telecom.config;

import com.example.telecom.enums.DistributionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Distribution {
    private DistributionType distributionType;
    private List<Map<String, Double>> args;
}
