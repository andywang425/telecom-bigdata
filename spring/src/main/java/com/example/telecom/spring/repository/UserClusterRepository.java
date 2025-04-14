package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.UserCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserClusterRepository extends JpaRepository<UserCluster, String> {
}
