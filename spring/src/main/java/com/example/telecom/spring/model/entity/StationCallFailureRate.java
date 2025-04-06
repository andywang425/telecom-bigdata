package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "station_call_failure_rate")
public class StationCallFailureRate {
    @EmbeddedId
    private YearMonthStationId id;

    @Column(nullable = false)
    private Long totalCall;

    private Long failedCall;

    private Double callFailureRate;
}