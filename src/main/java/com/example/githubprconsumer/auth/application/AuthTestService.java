package com.example.githubprconsumer.auth.application;

import com.example.githubprconsumer.auth.application.dto.TokenResponseDto;
import com.example.githubprconsumer.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthTestService {

    private final JwtProvider jwtProvider;

    private final MemberService memberService;

    @Transactional
    public TokenResponseDto testLogin(String login){
        memberService.createIfNotExist(login);
        String token = jwtProvider.createToken(login);
        return new TokenResponseDto(token);
    }
}
