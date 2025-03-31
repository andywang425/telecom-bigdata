package com.example.telecom.spring.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Table(name = "monthly_call_summary")
public class MonthlyCallSummary {
    @EmbeddedId
    private Id id;

    @Column(name = "total_duration_millis")
    private Long totalDurationMillis;

    @Column(name = "total_calls")
    private Long totalCalls;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public class Id implements Serializable {
        private Integer year;
        private Integer month;
    }
}

