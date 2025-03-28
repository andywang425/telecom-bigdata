package com.example.telecom.spring.controller;

import com.example.telecom.spring.common.BaseResponse;
import com.example.telecom.spring.common.ResultUtils;
import com.example.telecom.spring.model.dto.LoginRequest;
import com.example.telecom.spring.model.entity.User;
import com.example.telecom.spring.model.vo.UserVO;
import com.example.telecom.spring.service.UserService;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public BaseResponse<?> authenticateUser(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userService.getUserByEmail(userDetails.getUsername());
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);

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
}
