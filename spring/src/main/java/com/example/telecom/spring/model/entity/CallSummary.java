package com.example.telecom.spring.model.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "call_summary")
public class CallSummary {
    @EmbeddedId
    private YearMonth id;

    private Long totalDurationMillis;

    private Long totalCalls;
}

