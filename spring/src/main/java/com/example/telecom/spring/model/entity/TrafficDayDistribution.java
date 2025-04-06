package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "traffic_day_distribution")
public class TrafficDayDistribution {
    @EmbeddedId
    private YearMonthHour id;

    @Column(nullable = false)
    private Long sessionCount;

    private Long totalUpstreamDataVolume;

    private Long totalDownstreamDataVolume;
}