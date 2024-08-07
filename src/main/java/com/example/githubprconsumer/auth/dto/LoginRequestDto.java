package com.example.githubprconsumer.auth.dto;

import org.springframework.security.oauth2.core.user.OAuth2User;

public record LoginRequestDto(Long memberId, String nickname) {
    public static LoginRequestDto of(OAuth2User oAuth2User) {
        String oAuth2UserName = oAuth2User.getName();
        int id = Integer.parseInt(oAuth2UserName);
        Long githubMemberId = (long) id;
        String githubNickname = oAuth2User.getAttribute("login");
        return new LoginRequestDto(githubMemberId, githubNickname);
    }
}
