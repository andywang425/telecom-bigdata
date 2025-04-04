package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.CallSummary;
import com.example.telecom.spring.model.entity.YearMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CallRepository extends JpaRepository<CallSummary, YearMonth> {

    @Query("SELECT m.id.year AS year, SUM(m.totalCalls) AS totalCalls, ROUND(SUM(m.totalDurationMillis) / 60000) AS totalCallDuration " +
            "FROM CallSummary m " +
            "WHERE m.id.year BETWEEN :startYear AND :endYear " +
            "GROUP BY m.id.year ORDER BY m.id.year")
    List<YearlyCallSummary> findTotalCallsPerYear(@Param("startYear") int startYear,
                                                  @Param("endYear") int endYear);

    @Query("SELECT m.id.month AS month, m.totalCalls AS totalCalls, ROUND(m.totalDurationMillis / 60000) AS totalCallDuration " +
            "FROM CallSummary m " +
            "WHERE m.id.year = :year " +
            "ORDER BY m.id.month")
    List<MonthlyCallSummary> findTotalCallsPerMonth(@Param("year") int year);

    interface YearlyCallSummary {
        Integer getYear();

        Long getTotalCalls();

        Long getTotalCallDuration();
    }

    interface MonthlyCallSummary {
        Integer getMonth();

        Long getTotalCalls();

        Long getTotalCallDuration();
    }
}

