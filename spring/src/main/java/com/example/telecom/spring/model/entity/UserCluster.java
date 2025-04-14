package com.example.telecom.spring.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_cluster")
public class UserCluster {

    @Id
    private String phone;

    @Column(nullable = false)
    private Integer cluster;

    @Column(name = "pca_x", nullable = false)
    private Double pcaX;

    @Column(name = "pca_y", nullable = false)
    private Double pcaY;

}