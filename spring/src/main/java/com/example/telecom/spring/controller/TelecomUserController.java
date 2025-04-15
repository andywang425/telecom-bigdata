package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.ResponseUtils;
import com.example.telecom.spring.model.entity.UserCluster;
import com.example.telecom.spring.model.vo.UserClusterCountVO;
import com.example.telecom.spring.service.TelecomUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class TelecomUserController {

    private final TelecomUserService service;

    @GetMapping("/cluster")
    public BaseResponse<List<UserCluster>> getAll() {
        List<UserCluster> results = service.getAll();
        return ResponseUtils.success(results);
    }

    @GetMapping("/count")
    public BaseResponse<List<UserClusterCountVO>> getUserClusterCount() {
        List<UserClusterCountVO> results = service.getUserClusterCount();
        return ResponseUtils.success(results);
    }
}
