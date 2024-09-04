package com.example.githubprconsumer.auth.application;

import com.example.githubprconsumer.auth.domain.CustomOauth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserDetailService extends DefaultOAuth2UserService {

    @Override
    public CustomOauth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String login = (String) oAuth2User.getAttributes().get("login");

        if (login == null) {
            throw new OAuth2AuthenticationException("Login 어트리뷰트가 존재하지 않습니다.");
        }

        return new CustomOauth2User(login);
    }
}
