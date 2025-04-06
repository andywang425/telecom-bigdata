package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.StationCallFailureRate;
import com.example.telecom.spring.model.entity.YearMonthStationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationCallFailureRateRepository extends JpaRepository<StationCallFailureRate, YearMonthStationId> {
    @Query("SELECT m.id.year AS year, ROUND(SUM(m.failedCall) * 100.0 / SUM(m.totalCall), 2) AS callFailureRate " +
            "FROM StationCallFailureRate m " +
            "WHERE m.id.year BETWEEN :startYear AND :endYear " +
            "GROUP BY m.id.year ORDER BY m.id.year")
    List<YearlyCallFailureRate> findFailureRatePerYear(@Param("startYear") int startYear,
                                                       @Param("endYear") int endYear);

    @Query("SELECT m.id.month AS month, ROUND(SUM(m.failedCall) * 100.0 / SUM(m.totalCall), 2) AS callFailureRate " +
            "FROM StationCallFailureRate m " +
            "WHERE m.id.year = :year " +
            "GROUP BY m.id.month ORDER BY m.id.month")
    List<MonthlyCallFailureRate> findFailureRatePerMonth(@Param("year") int year);

    interface YearlyCallFailureRate {
        Integer getYear();

        Double getCallFailureRate();
    }

    interface MonthlyCallFailureRate {
        Integer getMonth();

        Double getCallFailureRate();
    }
}
