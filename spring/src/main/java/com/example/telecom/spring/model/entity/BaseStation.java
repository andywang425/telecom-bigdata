package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "base_stations")
public class BaseStation {
    @Id
    @Column(nullable = false)
    private String id;
}