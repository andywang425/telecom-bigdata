package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.TrafficDayDistribution;
import com.example.telecom.spring.model.entity.YearMonthHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrafficDayDistributionRepository extends JpaRepository<TrafficDayDistribution, YearMonthHour> {

    @Query("SELECT m.id.hour AS hour, m.sessionCount AS sessionCount, " +
            "ROUND(m.totalUpstreamDataVolume / 1024) AS totalUpstream, ROUND(m.totalDownstreamDataVolume / 1024) AS totalDownstream " +
            "FROM TrafficDayDistribution m " +
            "WHERE m.id.year = :year AND m.id.month = :month " +
            "ORDER BY m.id.hour")
    List<TrafficDayDistribution> findTrafficDayDistributionByMonth(int year, int month);

    interface TrafficDayDistribution {
        long getHour();

        Long getSessionCount();

        Long getTotalUpstream();

        Long getTotalDownstream();
    }
}

