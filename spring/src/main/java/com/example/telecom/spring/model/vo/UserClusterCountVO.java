package com.example.telecom.spring.model.vo;

import lombok.Data;

@Data
public class UserClusterCountVO {
    private Integer cluster;
    private Long count;
    private Double percentage;
}
