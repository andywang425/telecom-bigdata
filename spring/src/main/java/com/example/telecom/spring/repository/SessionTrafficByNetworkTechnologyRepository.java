package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.SessionTrafficByNetworkTechnology;
import com.example.telecom.spring.model.entity.YearMonthNetworkTechnology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionTrafficByNetworkTechnologyRepository extends JpaRepository<SessionTrafficByNetworkTechnology, YearMonthNetworkTechnology> {

    @Query("SELECT m.id.networkTechnology AS networkTechnology, m.sessionCount AS sessionCount, " +
            "ROUND(m.totalUpstreamDataVolume / 1024) AS totalUpstreamDataVolume, " +
            "ROUND(m.totalDownstreamDataVolume / 1024) AS totalDownstreamDataVolume " +
            "FROM SessionTrafficByNetworkTechnology m " +
            "WHERE m.id.year = :year AND m.id.month = :month")
    List<TrafficNetworkTechnology> findByYearMonth(int year, int month);

    interface TrafficNetworkTechnology {
        String getNetworkTechnology();

        Long getSessionCount();

        Long getTotalUpstreamDataVolume();

        Long getTotalDownstreamDataVolume();
    }
}

