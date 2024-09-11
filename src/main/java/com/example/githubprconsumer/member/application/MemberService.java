package com.example.githubprconsumer.member.application;

import com.example.githubprconsumer.member.domain.Member;
import com.example.githubprconsumer.member.domain.MemberException;
import com.example.githubprconsumer.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberByLogin(String login) {
        return memberRepository.findByLogin(login).orElseThrow(
                () -> new MemberException.MemberNotFoundException(login)
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

    /// 이 아래는 read

    public MemberResponseDto getMemberResponseDto(String login) {
        Member member = getMemberByLogin(login);
        return new MemberResponseDto(member.getId(), member.getLogin());
    }
}
