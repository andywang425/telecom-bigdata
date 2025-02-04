package com.example.telecom.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hadoop {
    private String hdfsURI;
    private String user;
    private String outputPath;
    private Map<String, String> config;
}
