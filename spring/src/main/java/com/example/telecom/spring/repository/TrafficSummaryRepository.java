package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.TrafficSummary;
import com.example.telecom.spring.model.entity.YearMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrafficSummaryRepository extends JpaRepository<TrafficSummary, YearMonth> {

    @Query("SELECT m.id.year AS year, SUM(m.totalSessions) AS totalSessions, ROUND(SUM(m.totalDuration) / 60000) AS totalDuration, " +
            "ROUND(SUM(m.totalUpstream) / 1024 / 1024) AS totalUpstream, ROUND(SUM(m.totalDownstream) / 1024 / 1024) AS totalDownstream " +
            "FROM TrafficSummary m " +
            "WHERE m.id.year BETWEEN :startYear AND :endYear " +
            "GROUP BY m.id.year ORDER BY m.id.year")
    List<YearlyTrafficSummary> findTotalTrafficPerYear(@Param("startYear") int startYear,
                                                       @Param("endYear") int endYear);

    @Query("SELECT m.id.month AS month, m.totalSessions AS totalSessions, ROUND(m.totalDuration / 60000) AS totalDuration, " +
            "ROUND(m.totalUpstream / 1024 / 1024) AS totalUpstream, ROUND(m.totalDownstream / 1024 / 1024) AS totalDownstream " +
            "FROM TrafficSummary m " +
            "WHERE m.id.year = :year " +
            "ORDER BY m.id.month")
    List<MonthlyTrafficSummary> findTotalTrafficPerMonth(@Param("year") int year);

    interface YearlyTrafficSummary {
        Integer getYear();

        Long getTotalSessions();

        Long getTotalDuration();

        Long getTotalUpstream();

        Long getTotalDownstream();
    }

    interface MonthlyTrafficSummary {
        Integer getMonth();

        Long getTotalSessions();

        Long getTotalDuration();

        Long getTotalUpstream();

        Long getTotalDownstream();
    }
}

