package com.example.githubprconsumer.auth.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public record CustomOauth2User(String login) implements OAuth2User {

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("login", this.login);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return login;
    }
}
