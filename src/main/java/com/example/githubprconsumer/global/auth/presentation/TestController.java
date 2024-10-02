package com.example.githubprconsumer.global.auth.presentation;

import com.example.githubprconsumer.global.auth.application.AuthTestService;
import com.example.githubprconsumer.global.auth.application.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class TestController {

    private final AuthTestService authTestService;

    @PostMapping("/tests/login")
    public TokenResponseDto testLogin(@RequestParam String login) {
        return authTestService.testLogin(login);
    }
}
