package com.example.telecom.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {
    private RecordNumber recordNumber;
    private DateRange dateRange;
    private Patterns patterns;
    private Hadoop hadoop;
    private CsvHeader csvHeader;
}

