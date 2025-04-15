package com.example.telecom.spring.repository;

import com.example.telecom.spring.model.entity.UserCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserClusterRepository extends JpaRepository<UserCluster, String> {

    @Query("SELECT m.cluster AS cluster, COUNT(*) AS count " +
            "FROM UserCluster m " +
            "GROUP BY m.cluster"
    )
    List<UserClusterCount> findUserClusterCount();

    interface UserClusterCount {
        Integer getCluster();

        Long getCount();
    }
}
