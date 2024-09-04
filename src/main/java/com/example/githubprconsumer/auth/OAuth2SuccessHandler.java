package com.example.githubprconsumer.auth;

import com.example.githubprconsumer.auth.application.JwtProvider;
import com.example.githubprconsumer.auth.domain.CustomOauth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Log4j2
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider tokenProvider;
    private static final String OAUTH_SUCCESS_URI = "/oauth2/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOauth2User oAuth2User = (CustomOauth2User) authentication.getPrincipal();
        String login = oAuth2User.login();
        log.info("[{}] 유저 로그인 요청", () -> login);
        String accessToken = tokenProvider.createToken(login);

        // 토큰 전달을 위한 redirect
        String redirectUrl = UriComponentsBuilder.fromUriString(OAUTH_SUCCESS_URI)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        log.info(redirectUrl);

        response.sendRedirect(redirectUrl);
    }
}