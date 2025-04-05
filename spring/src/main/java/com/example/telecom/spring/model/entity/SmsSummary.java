package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "sms_summary")
public class SmsSummary {
    @EmbeddedId
    private YearMonth id;

    @Column(nullable = false)
    private Long totalCount;

    private Long totalLength;
}