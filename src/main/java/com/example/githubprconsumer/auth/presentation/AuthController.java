package com.example.githubprconsumer.auth.presentation;

import com.example.githubprconsumer.auth.application.OAuth2Service;
import com.example.githubprconsumer.auth.application.dto.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/oauth2/login")
    @Operation(summary = "Oauth2 로그인을 수행한다")
    public TokenResponseDto oauth2Login(@RequestParam String code) {
        return oAuth2Service.login(code);
    }
}
