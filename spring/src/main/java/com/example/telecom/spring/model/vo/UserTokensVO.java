package com.example.telecom.spring.model.vo;

import lombok.Data;

import java.time.Instant;

@Data
public class UserTokensVO {
    private String accessToken;
    private Instant expiresAt;
    private String refreshToken;
}
