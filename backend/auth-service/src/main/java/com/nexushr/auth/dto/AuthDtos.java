package com.nexushr.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDtos {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be 8-128 characters")
        private String password;

        private String phoneNumber;
        private String tenantId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        private String mfaCode;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private long expiresIn;
        private UserInfo user;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private java.util.Set<String> roles;
        private boolean mfaEnabled;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RefreshTokenRequest {
        @NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MfaSetupResponse {
        private String secret;
        private String qrCodeUri;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ForgotPasswordRequest {
        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ResetPasswordRequest {
        @NotBlank(message = "Token is required")
        private String token;

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 128, message = "Password must be 8-128 characters")
        private String newPassword;
    }
}
