package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "traffic_summary")
public class TrafficSummary {
    @EmbeddedId
    private YearMonth id;

    @Column(nullable = false)
    private Long totalSessions;

    private Long totalDuration;

    private Long totalUpstream;

    private Long totalDownstream;
}