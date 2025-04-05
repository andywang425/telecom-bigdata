package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.SmsStatus;
import com.example.telecom.spring.model.entity.YearMonthSendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SmsStatusRepository extends JpaRepository<SmsStatus, YearMonthSendStatus> {

    @Query("SELECT m.id.sendStatus AS sendStatus, m.smsCount AS smsCount " +
            "FROM SmsStatus m " +
            "WHERE m.id.year = :year AND m.id.month = :month")
    List<SmsStatus> findSmsStatusByYearAndMonth(int year, int month);

    interface SmsStatus {
        String getSendStatus();

        long getSmsCount();
    }
}

