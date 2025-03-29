package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "monthly_call_summary")
public class MonthlyCallSummary {
    @EmbeddedId
    private MonthlyCallSummaryId id;

    @Column(name = "total_duration_millis")
    private Long totalDurationMillis;

    @Column(name = "total_calls")
    private Long totalCalls;
}

