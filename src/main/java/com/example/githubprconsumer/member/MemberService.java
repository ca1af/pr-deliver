package com.example.githubprconsumer.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void addAuthToken(Long memberId, String authToken){
        Member member = getMemberById(memberId);
        member.addToken(authToken);
    }

    public boolean existsByLogin(String login){
        return memberRepository.existsByLogin(login);
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
