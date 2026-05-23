package com.nexushr.auth.service;

import com.nexushr.auth.dto.AuthDtos.*;
import com.nexushr.auth.model.User;
import com.nexushr.auth.repository.UserRepository;
import com.nexushr.auth.security.JwtTokenProvider;
import com.nexushr.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @InjectMocks private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@nexushr.com")
                .password("$argon2id$hashed_password")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+919876543210")
                .status(User.UserStatus.ACTIVE)
                .roles(Set.of(User.Role.ROLE_EMPLOYEE))
                .failedLoginAttempts(0)
                .mfaEnabled(false)
                .build();
        testUser.setId("user-123");
        testUser.setTenantId("default");
    }

    @Test
    @DisplayName("Register: should create new user with hashed password")
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@nexushr.com");
        request.setPassword("StrongP@ss123");
        request.setFirstName("New");
        request.setLastName("User");

        when(userRepository.existsByEmail("new@nexushr.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongP@ss123")).thenReturn("$argon2id$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("new-user-id");
            return u;
        });
        when(jwtTokenProvider.generateAccessToken(any(), any(), any(), any())).thenReturn("access-jwt");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-jwt");
        when(jwtTokenProvider.getAccessTokenExpiry()).thenReturn(3600000L);

        AuthResponse response = authService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("access-jwt");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(passwordEncoder).encode("StrongP@ss123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register: should throw when email already exists")
    void register_DuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@nexushr.com");
        when(userRepository.existsByEmail("existing@nexushr.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("Login: should authenticate and return tokens")
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@nexushr.com");
        request.setPassword("correct_password");

        when(userRepository.findByEmail("test@nexushr.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("correct_password", testUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateAccessToken(any(), any(), any(), any())).thenReturn("access-jwt");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-jwt");
        when(jwtTokenProvider.getAccessTokenExpiry()).thenReturn(3600000L);

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("test@nexushr.com");
        verify(userRepository).save(argThat(u -> u.getFailedLoginAttempts() == 0));
    }

    @Test
    @DisplayName("Login: should increment failed attempts on wrong password")
    void login_WrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@nexushr.com");
        request.setPassword("wrong_password");

        when(userRepository.findByEmail("test@nexushr.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong_password", testUser.getPassword())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userRepository).save(argThat(u -> u.getFailedLoginAttempts() == 1));
    }

    @Test
    @DisplayName("Login: should lock account after 5 failed attempts")
    void login_AccountLock() {
        testUser.setFailedLoginAttempts(4);
        LoginRequest request = new LoginRequest();
        request.setEmail("test@nexushr.com");
        request.setPassword("wrong_password");

        when(userRepository.findByEmail("test@nexushr.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong_password", testUser.getPassword())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class);

        verify(userRepository).save(argThat(u ->
                u.getStatus() == User.UserStatus.LOCKED && u.getLockedUntil() != null));
    }

    @Test
    @DisplayName("Login: should reject locked account")
    void login_LockedAccount() {
        testUser.setStatus(User.UserStatus.LOCKED);
        testUser.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        LoginRequest request = new LoginRequest();
        request.setEmail("test@nexushr.com");
        request.setPassword("any");

        when(userRepository.findByEmail("test@nexushr.com")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("locked");
    }
}
