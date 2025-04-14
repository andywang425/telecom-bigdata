package com.example.telecom.spring.service;

import com.example.telecom.spring.model.entity.UserCluster;
import com.example.telecom.spring.repository.UserClusterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelecomUserService {

    private final UserClusterRepository userClusterRepository;

    public List<UserCluster> getAll() {
        return userClusterRepository.findAll();
    }
}
