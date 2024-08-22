package com.example.githubprconsumer.auth;

import com.example.githubprconsumer.auth.dto.LoginRequestDto;
import com.example.githubprconsumer.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    // 여기서도 동작부터 시작 해 보자.
    private final MemberService memberService;

    public void saveIfNotPresent(LoginRequestDto loginRequestDto) {
        memberService.createIfNotExist(loginRequestDto.nickname());
    }
}
