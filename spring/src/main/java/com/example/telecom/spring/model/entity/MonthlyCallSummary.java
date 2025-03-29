package com.example.telecom.spring.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

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

