package com.example.githubprconsumer.global.auth.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilsTest {

    @Test
    void replaceBearerPrefix_ShouldRemoveBearerPrefix() {
        // Given
        String bearerToken = "Bearer validtoken";

        // When
        String token = JwtUtils.replaceBearerPrefix(bearerToken);

        // Then
        assertThat(token).isEqualTo("validtoken");
    }

    @Test
    void replaceBearerPrefix_ShouldThrowExceptionWhenPrefixIsMissing() {
        // Given
        String invalidToken = "InvalidToken";

        // When & Then
        assertThatThrownBy(() -> JwtUtils.replaceBearerPrefix(invalidToken))
                .isInstanceOf(AuthenticationException.InvalidJwtTokenException.class);
    }

    @Test
    void isValidBearerToken_ShouldReturnTrueForValidToken() {
        // Given
        String validBearerToken = "Bearer validtoken";

        // When
        boolean isValid = JwtUtils.isValidBearerToken(validBearerToken);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void isValidBearerToken_ShouldReturnFalseForInvalidToken() {
        // Given
        String invalidBearerToken = "InvalidToken";

        // When
        boolean isValid = JwtUtils.isValidBearerToken(invalidBearerToken);

        // Then
        assertThat(isValid).isFalse();
    }
}
