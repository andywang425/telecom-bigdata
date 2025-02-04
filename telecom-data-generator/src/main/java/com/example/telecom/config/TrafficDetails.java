package com.example.telecom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficDetails {
    private Distribution startTime;
    private Distribution duration;
    private Distribution upstreamRate;
    private Distribution downstreamRate;
    private List<UserPreference> preference;
}
