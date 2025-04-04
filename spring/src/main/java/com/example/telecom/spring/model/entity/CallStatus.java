package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "call_status")
public class CallStatus {
    @EmbeddedId
    private YearMonthCallStatus id;

    @Column(nullable = false)
    private Long callCount;
}