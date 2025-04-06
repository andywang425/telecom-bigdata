package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.BaseStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseStationRepository extends JpaRepository<BaseStation, String> {

}
