package com.example.githubprconsumer.securitytest;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2UserSecurityContextFactory.class)
public @interface WithMockOAuth2User {
    String username() default "testuser";
    String nameAttributeKey() default "name";
    String[] authorities() default {"ROLE_USER"};
    String[] attributes() default {"name:123", "email:testuser@example.com"};
}