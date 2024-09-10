package com.example.githubprconsumer.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/me")
    @Operation(description = "회원 스스로의 정보를 리턴한다.")
    public MemberResponseDto getMyInfo(@AuthenticationPrincipal OAuth2User customOauth2User) {
        return memberService.getMemberResponseDto(customOauth2User.getName());
    }
}
