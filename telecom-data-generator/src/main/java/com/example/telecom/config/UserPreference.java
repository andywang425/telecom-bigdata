package com.example.telecom.config;

import com.example.telecom.enums.ApplicationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {
    private ApplicationType name;
    private double weight;
}
