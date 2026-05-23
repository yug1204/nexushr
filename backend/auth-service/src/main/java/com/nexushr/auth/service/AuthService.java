package com.nexushr.auth.service;

import com.nexushr.auth.dto.AuthDtos.*;
import com.nexushr.auth.model.User;
import com.nexushr.auth.repository.UserRepository;
import com.nexushr.auth.security.JwtTokenProvider;
import com.nexushr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .status(User.UserStatus.ACTIVE)
                .roles(Set.of(User.Role.ROLE_EMPLOYEE))
                .build();
        user.setTenantId(request.getTenantId() != null ? request.getTenantId() : "default");

        user = userRepository.save(user);
        log.info("User registered: {}", user.getEmail());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        if (user.isAccountLocked()) {
            throw new BusinessException("Account is locked. Try again later.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
                user.setStatus(User.UserStatus.LOCKED);
            }
            userRepository.save(user);
            throw new BusinessException("Invalid credentials");
        }

        // Reset failed attempts on successful login
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(LocalDateTime.now());
        user.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(user);

        return generateAuthResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        var claims = jwtTokenProvider.validateToken(request.getRefreshToken());
        String userId = claims.getSubject();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        return generateAuthResponse(user);
    }

    public void logout(String token) {
        jwtTokenProvider.blacklistToken(token);
        log.info("Token blacklisted");
    }

    private AuthResponse generateAuthResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), roleNames, user.getTenantId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiry() / 1000)
                .user(UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .roles(roleNames)
                        .mfaEnabled(user.isMfaEnabled())
                        .build())
                .build();
    }
}
