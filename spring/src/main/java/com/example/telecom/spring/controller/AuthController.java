package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.JwtUtil;
import com.example.telecom.spring.common.ResultUtils;
import com.example.telecom.spring.model.dto.LoginRequest;
import com.example.telecom.spring.model.entity.User;
import com.example.telecom.spring.model.vo.UserVO;
import com.example.telecom.spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.HashMap;
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

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public BaseResponse<?> authenticateUser(@RequestBody LoginRequest request) {
        try {
            logger.info("登录: {}", Instant.now());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtil.generateToken(userDetails.getUsername());
            String refreshToken = UUID.randomUUID().toString();

            User user = userService.getUserByEmail(userDetails.getUsername());
            user.setAccessToken(jwtToken);
            user.setRefreshToken(refreshToken);
            user.setExpiresAt(Instant.now().plus(10, ChronoUnit.SECONDS));
            userService.userRepository.save(user);

            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            logger.info("用户登录成功，用户名：{}，token：{}", userDetails.getUsername(), jwtToken);


            return ResultUtils.success(userVO);
        } catch (AuthenticationException e) {
            return ResultUtils.error(401, "用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public BaseResponse<?> registerUser(@RequestBody LoginRequest request) {
        if (userService.getUserByEmail(request.getEmail()) != null) {
            return ResultUtils.error(400, "邮箱已被注册");
        }

        User user = userService.registerUser(request.getEmail(), request.getPassword(), passwordEncoder);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return ResultUtils.success(userVO);
    }

    @PostMapping("/refresh-token")
    public BaseResponse<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        logger.info("收到刷新令牌请求，refreshToken: {}", request.get("refreshToken"));
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return ResultUtils.error(400, "Refresh token required");
        }

        Optional<User> userOpt = userService.userRepository.findByRefreshToken(refreshToken);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

//            if (user.getExpiresAt().isBefore(Instant.now())) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
//            }

            String jwtToken = jwtUtil.generateToken(user.getEmail());
            refreshToken = UUID.randomUUID().toString();

            user.setAccessToken(jwtToken);
            user.setRefreshToken(refreshToken);
            user.setExpiresAt(Instant.now().plus(10, ChronoUnit.SECONDS));
            userService.userRepository.save(user);

            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);


            return ResultUtils.success(userVO);
        } else {
            return ResultUtils.error(401, "Invalid refresh token");
        }
    }
}
