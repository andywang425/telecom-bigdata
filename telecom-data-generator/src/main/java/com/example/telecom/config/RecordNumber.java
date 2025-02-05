package com.example.telecom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordNumber {
    private long call;
    private long sms;
    private long traffic;
}
