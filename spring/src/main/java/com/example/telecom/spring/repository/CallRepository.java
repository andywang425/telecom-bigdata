package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.CallSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CallRepository extends JpaRepository<CallSummary, CallSummary.Id> {

    @Query("SELECT m.id.year AS year, SUM(m.totalCalls) AS totalCalls " +
            "FROM CallSummary m " +
            "WHERE m.id.year BETWEEN :startYear AND :endYear " +
            "GROUP BY m.id.year ORDER BY m.id.year")
    List<YearlyCallSummary> findTotalCallsPerYear(@Param("startYear") int startYear,
                                                  @Param("endYear") int endYear);

    interface YearlyCallSummary {
        Integer getYear();

        Long getTotalCalls();
    }
}

