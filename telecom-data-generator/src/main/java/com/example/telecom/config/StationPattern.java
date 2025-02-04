package com.example.telecom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationPattern {
    private Map<String, Double> technologyWeight;
    private List<StationFailureInfo> failureInfo;
}
