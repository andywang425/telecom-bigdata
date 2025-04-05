package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.CallStatus;
import com.example.telecom.spring.model.entity.YearMonthCallStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CallStatusRepository extends JpaRepository<CallStatus, YearMonthCallStatus> {

    @Query("SELECT m.id.year AS year, m.id.callStatus AS callStatus, m.callCount AS callCount " +
            "FROM CallStatus m " +
            "WHERE m.id.year = :year AND m.id.month = :month")
    List<CallStatus> findCallStatusByYearAndMonth(int year, int month);

    interface CallStatus {
        String getCallStatus();

        long getCallCount();
    }
}

