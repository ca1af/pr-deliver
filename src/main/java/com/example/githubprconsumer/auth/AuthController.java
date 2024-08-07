package com.example.githubprconsumer.auth;

import com.example.githubprconsumer.auth.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthService authService;


    @GetMapping("/oauth2/login")
    public void oauth2Login(@AuthenticationPrincipal OAuth2User oAuth2User) {
        log.info("[{}] 유저 로그인 \n 상세정보 : {}", () -> oAuth2User.getAttribute("login"), oAuth2User::toString);
        authService.saveIfNotPresent(LoginRequestDto.of(oAuth2User));
    }
}
