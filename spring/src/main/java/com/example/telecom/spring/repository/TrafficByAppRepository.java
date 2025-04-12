package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.TrafficByApp;
import com.example.telecom.spring.model.entity.YearMonthApplicationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrafficByAppRepository extends JpaRepository<TrafficByApp, YearMonthApplicationType> {

    @Query("SELECT m.id.applicationType AS applicationType, m.sessionCount AS sessionCount, " +
            "ROUND(m.totalUpstreamDataVolume / 1024) AS totalUpstreamDataVolume, " +
            "ROUND(m.totalDownstreamDataVolume / 1024) AS totalDownstreamDataVolume " +
            "FROM TrafficByApp m " +
            "WHERE m.id.year = :year AND m.id.month = :month")
    List<TrafficApplicationType> findByYearMonth(int year, int month);

    interface TrafficApplicationType {
        String getApplicationType();

        Long getSessionCount();

        Long getTotalUpstreamDataVolume();

        Long getTotalDownstreamDataVolume();
    }
}

