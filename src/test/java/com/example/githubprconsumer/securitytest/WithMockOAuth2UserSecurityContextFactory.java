package com.example.githubprconsumer.securitytest;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class WithMockOAuth2UserSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2User> {

    @Override
    public SecurityContext createSecurityContext(WithMockOAuth2User withMockOAuth2User) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Map<String, Object> attributes = Arrays.stream(withMockOAuth2User.attributes())
                .map(attr -> attr.split(":"))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));

        OAuth2User principal = new DefaultOAuth2User(
                Arrays.stream(withMockOAuth2User.authorities())
                        .map(SimpleGrantedAuthority::new)
                        .toList(),
                attributes,
                withMockOAuth2User.nameAttributeKey()
        );

        context.setAuthentication(new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "github"));

        return context;
    }
}