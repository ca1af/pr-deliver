package com.example.githubprconsumer.auth.application;

import com.example.githubprconsumer.auth.domain.AuthenticationException;
import com.example.githubprconsumer.auth.domain.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private static final String TEST_USER_LOGIN = "testUserLogin";
    private JwtProvider jwtProvider;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        String secretKey = "defaultSecretKeyForTestingPurposesOnly12345";
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtProvider, "key", key);
        ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
        jwtProvider.init();
    }

    @Test
    @DisplayName("토큰을 생성할 수 있다.")
    void createToken() {
        // when
        String token = jwtProvider.createToken(TEST_USER_LOGIN);

        // BASE64 DECODE 를 위해 Bearer prefix 제거
        token = JwtUtils.replaceBearerPrefix(token);

        // then
        assertThat(token).isNotNull();
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        assertThat(claims.getSubject()).isEqualTo(TEST_USER_LOGIN);
    }

    @Test
    @DisplayName("토큰에서 인증 정보를 가져올 수 있다.")
    void getAuthentication() {
        // given
        String token = jwtProvider.createToken(TEST_USER_LOGIN);

        // when
        Authentication authentication = jwtProvider.getAuthentication(token);

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(TEST_USER_LOGIN);
    }

    @Test
    @DisplayName("토큰에서 로그인 정보를 가져올 수 있다.")
    void getLoginFromToken() {
        // given
        String token = jwtProvider.createToken(TEST_USER_LOGIN);

        // when
        String login = jwtProvider.getLoginFromToken(token);

        // then
        assertThat(login).isEqualTo(TEST_USER_LOGIN);
    }

    @Test
    @DisplayName("잘못된 토큰에서 로그인 정보를 가져올 때 예외를 처리한다.")
    void getLoginFromToken_InvalidToken() {
        // given
        String invalidToken = "invalidToken";

        // when & then
        assertThatThrownBy(() -> jwtProvider.getLoginFromToken(invalidToken))
                .isInstanceOf(AuthenticationException.InvalidJwtTokenException.class);
    }

    @Test
    @DisplayName("HTTP 요청에서 토큰을 추출할 수 있다.")
    void resolveToken() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer testToken");

        // when
        String token = jwtProvider.resolveToken(request);

        // then
        assertThat(token).isEqualTo("Bearer testToken");
    }

    @Test
    @DisplayName("유효한 토큰인지 확인할 수 있다.")
    void isValidToken() {
        // given
        String token = jwtProvider.createToken(TEST_USER_LOGIN);

        // when
        boolean isValid = jwtProvider.isValidToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰을 확인할 때 예외를 처리한다.")
    void isValidToken_ExpiredToken() {
        // given
        String expiredToken = Jwts.builder()
                .setSubject(TEST_USER_LOGIN)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 6))
                .setExpiration(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 5))
                .signWith(key)
                .compact();

        // when & then
        assertThatThrownBy(() -> jwtProvider.isValidToken(expiredToken))
                .isInstanceOf(AuthenticationException.InvalidJwtTokenException.class);
    }

    @Test
    @DisplayName("토큰이 유효하지 않을 때 예외를 처리한다.")
    void isValidToken_InvalidToken() {
        // given
        String invalidToken = "invalidToken";

        // when & then
        assertThatThrownBy(() -> jwtProvider.isValidToken(invalidToken))
                .isInstanceOf(AuthenticationException.InvalidJwtTokenException.class);
    }
}
