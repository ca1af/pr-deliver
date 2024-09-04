package com.example.githubprconsumer.member;

import com.example.githubprconsumer.auth.domain.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/me")
    public MemberResponseDto getMyInfo(@AuthenticationPrincipal CustomOauth2User customOauth2User) {
        return memberService.getMemberResponseDto(customOauth2User.login());
    }
}
