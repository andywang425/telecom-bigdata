package com.example.telecom.spring.service;

import com.example.telecom.spring.model.entity.UserCluster;
import com.example.telecom.spring.model.vo.UserClusterCountVO;
import com.example.telecom.spring.repository.UserClusterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelecomUserService {

    private final UserClusterRepository userClusterRepository;

    public List<UserCluster> getAll() {
        return userClusterRepository.findAll();
    }

    public Long getCount() {
        return userClusterRepository.count();
    }

    public List<UserClusterCountVO> getUserClusterCount() {
        Long totalCount = getCount();

        List<UserClusterRepository.UserClusterCount> list = userClusterRepository.findUserClusterCount();

        return list.stream().map(item -> {
            UserClusterCountVO userClusterCountVO = new UserClusterCountVO();
            userClusterCountVO.setCluster(item.getCluster());
            userClusterCountVO.setCount(item.getCount());
            // 计算百分比并保留两位小数
            double percentage = item.getCount() * 100.0 / totalCount;
            BigDecimal roundedValue = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);
            userClusterCountVO.setPercentage(roundedValue.doubleValue());

            return userClusterCountVO;
        }).toList();
    }
}
