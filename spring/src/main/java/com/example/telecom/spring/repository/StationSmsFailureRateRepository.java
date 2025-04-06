package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.StationSmsFailureRate;
import com.example.telecom.spring.model.entity.YearMonthStationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationSmsFailureRateRepository extends JpaRepository<StationSmsFailureRate, YearMonthStationId> {
    @Query("SELECT m.id.year AS year, ROUND(SUM(m.failedSms) * 100.0 / SUM(m.totalSms), 2) AS smsFailureRate " +
            "FROM StationSmsFailureRate m " +
            "WHERE m.id.year BETWEEN :startYear AND :endYear " +
            "GROUP BY m.id.year ORDER BY m.id.year")
    List<YearlySmsFailureRate> findFailureRatePerYear(@Param("startYear") int startYear,
                                                      @Param("endYear") int endYear);

    @Query("SELECT m.id.month AS month, ROUND(SUM(m.failedSms) * 100.0 / SUM(m.totalSms), 2) AS smsFailureRate " +
            "FROM StationSmsFailureRate m " +
            "WHERE m.id.year = :year " +
            "GROUP BY m.id.month ORDER BY m.id.month")
    List<MonthlySmsFailureRate> findFailureRatePerMonth(@Param("year") int year);

    interface YearlySmsFailureRate {
        Integer getYear();

        Double getSmsFailureRate();
    }

    interface MonthlySmsFailureRate {
        Integer getMonth();

        Double getSmsFailureRate();
    }
}
