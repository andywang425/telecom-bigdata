package com.example.telecom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallDetails {
    private Distribution startTime;
    private Distribution duration;
    private List<CallStatusWeight> statusWeight;
}
