package com.example.telecom.config;

import com.example.telecom.enums.CallStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallStatusWeight {
    private CallStatus name;
    private double weight;
}
