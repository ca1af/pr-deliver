package com.example.githubprconsumer.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberByLogin(String nickname) {
        return memberRepository.findByLogin(nickname).orElseThrow(
                () -> new MemberException.MemberNotFoundException(nickname)
        );
    }

    public Member getMemberById(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException.MemberNotFoundException(memberId.toString())
        );
    }

    public boolean existsByLogin(String login){
        return memberRepository.existsByLogin(login);
    }

    public Member createIfNotExist(String login) {
        return memberRepository.findByLogin(login)
                .orElseGet(() -> save(new SignupRequestDto(login)));
    }

    private Member save(SignupRequestDto signupRequestDto) {
        Member member = signupRequestDto.toEntity();
        return memberRepository.save(member);
    }
}
