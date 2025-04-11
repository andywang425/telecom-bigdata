package com.example.telecom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {
    private RecordNumber recordNumber;
    private DateWeight dateWeight;
    private Patterns patterns;
    private Output output;
}

