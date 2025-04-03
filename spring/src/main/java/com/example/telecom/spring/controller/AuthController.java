package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.ResponseUtils;
import com.example.telecom.spring.jwt.JwtUtil;
import com.example.telecom.spring.model.dto.LoginRequest;
import com.example.telecom.spring.model.entity.User;
import com.example.telecom.spring.model.vo.UserTokensVO;
import com.example.telecom.spring.model.vo.UserVO;
import com.example.telecom.spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public BaseResponse<?> authenticateUser(@RequestBody LoginRequest request) {
        try {
            // 身份验证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // 生成JWT和刷新令牌
            Instant expiration = Instant.now().plus(1, ChronoUnit.HOURS);
            String jwtToken = JwtUtil.generateToken(userDetails.getUsername(), Date.from(expiration));
            String refreshToken = UUID.randomUUID().toString();

            // 保存刷新令牌到数据库
            User user = userService.getUserByEmail(userDetails.getUsername());
            user.setRefreshToken(refreshToken);
            userService.userRepository.save(user);

            // 构建响应，返回用户信息和令牌信息
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVO.setAccessToken(jwtToken);
            userVO.setExpiresAt(expiration);

            logger.info("用户登录成功，{}", user);

            return ResponseUtils.success(userVO);
        } catch (AuthenticationException e) {
            return ResponseUtils.error(401, "用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public BaseResponse<?> registerUser(@RequestBody LoginRequest request) {
        if (userService.getUserByEmail(request.getEmail()) != null) {
            return ResponseUtils.error(400, "邮箱已被注册");
        }

        User user = userService.registerUser(request.getEmail(), request.getPassword(), passwordEncoder);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        logger.info("用户注册成功，{}", user);

        return ResponseUtils.success(userVO);
    }

    @PostMapping("/refresh-token")
    public BaseResponse<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return ResponseUtils.error(400, "Refresh token required");
        }

        logger.info("用户请求刷新令牌，{}", refreshToken);

        Optional<User> userOpt = userService.userRepository.findByRefreshToken(refreshToken);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            logger.info("用户即将刷新令牌，{}", user);

            // 生成新的JWT和刷新令牌
            Instant expiration = Instant.now().plus(1, ChronoUnit.HOURS);
            String jwtToken = JwtUtil.generateToken(user.getEmail(), Date.from(expiration));
            refreshToken = UUID.randomUUID().toString();

            // 保存新的刷新令牌到数据库
            user.setRefreshToken(refreshToken);
            userService.userRepository.save(user);

            // 构建响应，返回新的令牌信息
            UserTokensVO userTokensVO = new UserTokensVO();
            userTokensVO.setAccessToken(jwtToken);
            userTokensVO.setRefreshToken(refreshToken);
            userTokensVO.setExpiresAt(expiration);

            logger.info("用户刷新令牌成功，{}", user);

            return ResponseUtils.success(userTokensVO);
        } else {
            return ResponseUtils.error(401, "Invalid refresh token");
        }
    }
}
