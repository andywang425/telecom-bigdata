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
@Table(name = "sms_day_distribution")
public class SmsDayDistribution {
    @EmbeddedId
    private YearMonthHour id;

    @Column(nullable = false)
    private Long smsCount;
}