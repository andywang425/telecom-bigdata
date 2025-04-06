package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "station_sms_failure_rate")
public class StationSmsFailureRate {
    @EmbeddedId
    private YearMonthStationId id;

    @Column(nullable = false)
    private Long totalSms;

    private Long failedSms;

    private Double smsFailureRate;
}