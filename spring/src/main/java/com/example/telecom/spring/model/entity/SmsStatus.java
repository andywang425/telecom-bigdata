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

    @Column(name = "sms_status_count", nullable = false)
    private Long smsCount;
}