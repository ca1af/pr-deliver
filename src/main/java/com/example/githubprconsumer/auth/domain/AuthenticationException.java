package com.example.githubprconsumer.auth.domain;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }

    public static class InvalidJWTPayloadException extends AuthenticationException {
        private static final String MISSING_PAYLOAD = "JWT Payload 'Login' 누락";
        public InvalidJWTPayloadException() {
            super(MISSING_PAYLOAD);
        }
    }

    public static class InvalidJwtTokenException extends AuthenticationException {
        private static final String INVALID_JWT = "유효하지 않은 토큰입니다";
        public InvalidJwtTokenException() {
            super(INVALID_JWT);
        }
    }
}
