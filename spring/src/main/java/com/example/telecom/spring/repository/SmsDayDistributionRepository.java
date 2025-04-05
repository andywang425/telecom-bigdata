package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.SmsDayDistribution;
import com.example.telecom.spring.model.entity.YearMonthHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SmsDayDistributionRepository extends JpaRepository<SmsDayDistribution, YearMonthHour> {

    @Query("SELECT m.id.hour AS hour, m.smsCount AS smsCount " +
            "FROM SmsDayDistribution m " +
            "WHERE m.id.year = :year AND m.id.month = :month " +
            "ORDER BY m.id.hour")
    List<SmsDayDistribution> findSmsDayDistributionByMonth(int year, int month);

    interface SmsDayDistribution {
        long getHour();

        long getSmsCount();
    }
}

