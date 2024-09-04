package com.example.githubprconsumer.securitytest.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class IfRequiredSecurityConfig {
    @Bean
    @Primary
    public SecurityFilterChain config(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(a -> a.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .build();
    }
}
