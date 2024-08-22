package com.example.githubprconsumer.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("로그인으로 회원을 조회할 때, 존재하는 회원을 정상적으로 찾는다.")
    void testGetMemberByLogin() {
        // Given
        String githubEmail = "test@example.com";
        Member member = new Member(999999L, githubEmail);
        memberRepository.save(member);

        // When
        Member foundMember = memberService.getMemberByLogin(githubEmail);

        // Then
        assertThat(member).isEqualTo(foundMember);
    }

    @Test
    @DisplayName("로그인으로 회원을 조회할 때, 존재하지 않는 회원이면 예외가 발생한다.")
    void testGetMember_ByLogin_NotFound() {
        // Given
        String githubEmail = "nonexistent@example.com";

        // When & Then
        assertThatThrownBy(() -> memberService.getMemberByLogin(githubEmail))
                .isInstanceOf(MemberException.MemberNotFoundException.class)
                .hasMessage("회원을 찾을 수 없습니다. 회원 이메일 : " + githubEmail);
    }

    @Test
    @DisplayName("회원이 존재하면, 새로운 회원을 생성하지 않고 기존 회원을 반환한다.")
    void testCreateIfNotExist_MemberExists() {
        // Given
        String githubEmail = "test@example.com";
        Member existingMember = new Member(999999L, githubEmail);
        memberRepository.save(existingMember);

        // When
        Member result = memberService.createIfNotExist(999999L, githubEmail);

        // Then
        assertThat(existingMember).isEqualTo(result);
    }

    @Test
    @DisplayName("회원이 존재하지 않으면, 새로운 회원을 생성한다.")
    void testCreateIfNotExist_MemberDoesNotExist() {
        // Given
        String githubEmail = "test@example.com";
        Long memberId = 99999998L;

        // When
        Member result = memberService.createIfNotExist(memberId, githubEmail);

        // Then
        assertThat(result.getId()).isEqualTo(memberId);
        assertThat(result.getLogin()).isEqualTo(githubEmail);

        Optional<Member> savedMember = memberRepository.findById(memberId);
        assertThat(savedMember).isPresent();
    }
}
