package com.example.githubprconsumer.global.auth.application;

import com.example.githubprconsumer.global.auth.domain.CustomOauth2User;
import com.example.githubprconsumer.member.application.MemberService;
import com.example.githubprconsumer.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserDetailService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    @Override
    public CustomOauth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String login = (String) oAuth2User.getAttributes().get("login");

        if (login == null) {
            throw new OAuth2AuthenticationException("Login 어트리뷰트가 존재하지 않습니다.");
        }

        Member member = memberService.createIfNotExist(login);

        return new CustomOauth2User(member.getLogin());
    }
}
