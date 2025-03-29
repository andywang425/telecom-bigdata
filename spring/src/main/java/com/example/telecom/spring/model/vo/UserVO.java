package com.example.telecom.spring.model.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
public class UserVO {
    private Long id;
    private String email;
    private Timestamp createdAt;
    private String accessToken;
    private Instant expiresAt;
    private String refreshToken;
}
