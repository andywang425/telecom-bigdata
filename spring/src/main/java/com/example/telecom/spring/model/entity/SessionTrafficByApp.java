package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "session_traffic_by_app")
public class SessionTrafficByApp {
    @EmbeddedId
    private YearMonthApplicationType id;

    @Column(nullable = false)
    private Long sessionCount;

    private Long totalUpstreamDataVolume;

    private Long totalDownstreamDataVolume;

}