package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.SmsSummary;
import com.example.telecom.spring.model.entity.YearMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SmsSummaryRepository extends JpaRepository<SmsSummary, YearMonth> {

    @Query("SELECT m.id.year AS year, SUM(m.totalCount) AS totalCount, SUM(m.totalLength) AS totalLength " +
            "FROM SmsSummary m " +
            "WHERE m.id.year BETWEEN :startYear AND :endYear " +
            "GROUP BY m.id.year ORDER BY m.id.year")
    List<YearlySmsSummary> findTotalSmsPerYear(@Param("startYear") int startYear,
                                               @Param("endYear") int endYear);

    @Query("SELECT m.id.month AS month, m.totalCount AS totalCount, m.totalLength AS totalLength " +
            "FROM SmsSummary m " +
            "WHERE m.id.year = :year " +
            "ORDER BY m.id.month")
    List<MonthlySmsSummary> findTotalSmsPerMonth(@Param("year") int year);

    interface YearlySmsSummary {
        Integer getYear();

        Long getTotalCount();

        Long getTotalLength();
    }

    interface MonthlySmsSummary {
        Integer getMonth();

        Long getTotalCount();

        Long getTotalLength();
    }
}

