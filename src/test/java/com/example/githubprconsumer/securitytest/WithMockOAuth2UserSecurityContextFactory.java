package com.example.githubprconsumer.securitytest;

import com.example.githubprconsumer.auth.CustomOauth2User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

        CustomOauth2User principal = new CustomOauth2User(
                (String) attributes.get(withMockOAuth2User.nameAttributeKey())
        );

        context.setAuthentication(new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "github"));


        return context;
    }
}