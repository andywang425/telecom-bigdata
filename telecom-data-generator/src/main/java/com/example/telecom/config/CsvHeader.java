package com.example.telecom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvHeader {
    private List<String> call;
    private List<String> sms;
    private List<String> traffic;
}
