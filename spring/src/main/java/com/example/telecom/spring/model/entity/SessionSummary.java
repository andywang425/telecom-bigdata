package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "session_summary")
public class SessionSummary {
    @EmbeddedId
    private YearMonth id;

    @Column(nullable = false)
    private Long totalSessions;

    private Long totalDuration;

    private Long totalUpstream;

    private Long totalDownstream;
}