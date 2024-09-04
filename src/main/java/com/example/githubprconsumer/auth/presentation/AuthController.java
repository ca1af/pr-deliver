package com.example.githubprconsumer.auth.presentation;

import com.example.githubprconsumer.auth.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    // /oauth2/login 경로로 요청을 보내야 하며, 성공에 대한 Handler 이다.
    @GetMapping("/oauth2/success")
    public TokenResponseDto oauth2Login(@RequestParam String accessToken) {
        log.info("OAuth2 access token: {}", accessToken);
        return new TokenResponseDto(accessToken);
    }
}
