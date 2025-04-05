package com.example.telecom.spring.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class YearMonthApplicationType implements Serializable {
    private Integer year;
    private Integer month;
    private String applicationType;
}
