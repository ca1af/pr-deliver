package com.example.githubprconsumer.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(
                () -> new MemberException.MemberNotFoundException(nickname)
        );
    }

    public Member createIfNotExist(Long memberId, String githubEmail) {
        return memberRepository.findById(memberId)
                .orElseGet(() -> save(new SignupRequestDto(memberId, githubEmail)));
    }

    private Member save(SignupRequestDto signupRequestDto) {
        Member member = signupRequestDto.toEntity();
        return memberRepository.save(member);
    }
}
