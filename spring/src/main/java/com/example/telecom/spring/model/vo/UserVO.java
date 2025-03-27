package com.example.telecom.spring.model.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserVO {
    private Long id;
    private String email;
    private Timestamp createdAt;
}
