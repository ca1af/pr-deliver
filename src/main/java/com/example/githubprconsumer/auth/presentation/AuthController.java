package com.example.githubprconsumer.auth.presentation;

import com.example.githubprconsumer.auth.dto.TokenResponseDto;
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

    // /oauth2/login 경로로 요청을 보내야 하며, 성공에 대한 Handler 이다.
    @GetMapping("/oauth2/success")
    @Operation(description = "필터의 SuccessHandler 설정을 위해 존재하는 API, Oauth2 인증 완료 시 사용한다.")
    public TokenResponseDto oauth2Login(@RequestParam String accessToken) {
        log.info("OAuth2 access token: {}", accessToken);
        return new TokenResponseDto(accessToken);
    }
}
