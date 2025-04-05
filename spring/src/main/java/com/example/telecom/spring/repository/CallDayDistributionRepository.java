package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.CallDayDistribution;
import com.example.telecom.spring.model.entity.YearMonthHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CallDayDistributionRepository extends JpaRepository<CallDayDistribution, YearMonthHour> {

    @Query("SELECT m.id.hour AS hour, m.callCount AS callCount " +
            "FROM CallDayDistribution m " +
            "WHERE m.id.year = :year AND m.id.month = :month " +
            "ORDER BY m.id.hour")
    List<CallDayDistribution> findCallDayDistributionByMonth(int year, int month);

    interface CallDayDistribution {
        long getHour();

        long getCallCount();
    }
}

