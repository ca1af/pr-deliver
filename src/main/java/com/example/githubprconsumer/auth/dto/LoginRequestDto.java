package com.example.githubprconsumer.auth.dto;

import org.springframework.security.oauth2.core.user.OAuth2User;

public record LoginRequestDto(String login) {
    public static LoginRequestDto of(OAuth2User oAuth2User) {
        String githubNickname = oAuth2User.getAttribute("login");
        return new LoginRequestDto(githubNickname);
    }
}
