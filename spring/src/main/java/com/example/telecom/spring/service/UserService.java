package com.example.telecom.spring.service;

import com.example.telecom.spring.model.entity.User;
import com.example.telecom.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    public final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    public User registerUser(String email, String rawPassword, PasswordEncoder encoder) {
        User user = User.builder()
                .email(email)
                .password(encoder.encode(rawPassword))
                .build();
        return userRepository.save(user);
    }
}