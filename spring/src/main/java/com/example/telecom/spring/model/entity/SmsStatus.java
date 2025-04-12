package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "sms_status")
public class SmsStatus {
    @EmbeddedId
    private YearMonthSendStatus id;

    @Column(nullable = false)
    private Long smsCount;
}