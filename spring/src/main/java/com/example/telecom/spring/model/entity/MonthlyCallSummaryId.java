package com.example.telecom.spring.model.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MonthlyCallSummaryId implements Serializable {
    private Integer year;
    private Integer month;
}
