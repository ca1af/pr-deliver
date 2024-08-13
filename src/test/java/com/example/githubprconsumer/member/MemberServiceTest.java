package com.example.githubprconsumer.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MemberServiceTest {

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    void testGetMemberByLogin() {
        // arrange
        String githubEmail = "test@example.com";
        Member member = new Member(1L, githubEmail);
        when(memberRepository.findByLogin(githubEmail)).thenReturn(Optional.of(member));

        // act
        Member foundMember = memberService.getMemberByLogin(githubEmail);

        // assert
        assertThat(member).isEqualTo(foundMember);
        verify(memberRepository, times(1)).findByLogin(githubEmail);
    }

    @Test
    void testGetMember_ByNickname_NotFound() {
        // arrange
        String githubEmail = "test@example.com";
        when(memberRepository.findByLogin(githubEmail)).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> memberService.getMemberByLogin(githubEmail))
                .isInstanceOf(MemberException.MemberNotFoundException.class)
                .hasMessage("회원을 찾을 수 없습니다. 회원 이메일 : " + githubEmail);

        verify(memberRepository, times(1)).findByLogin(githubEmail);
    }

    @Test
    void testCreateIfNotExist_MemberExists() {
        // arrange
        String nickname = "test@example.com";
        Member existingMember = new Member(1L, nickname);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));

        // act
        Member result = memberService.createIfNotExist(1L, nickname);

        // assert
        assertThat(existingMember).isEqualTo(result);
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testCreateIfNotExist_MemberDoesNotExist() {
        // arrange
        String githubEmail = "test@example.com";
        Member newMember = new Member(1L, githubEmail);
        when(memberRepository.findByLogin(githubEmail)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(newMember);

        // act
        Member result = memberService.createIfNotExist(1L, githubEmail);

        // assert
        assertThat(newMember).isEqualTo(result);
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }
}