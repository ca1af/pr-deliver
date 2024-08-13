package com.example.githubprconsumer.member;

import com.example.githubprconsumer.global.TimeStamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(1L, "testNickname");
    }

    @Test
    void testConstructor() {
        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getLogin()).isEqualTo("testNickname");
        assertThat(member.isValid()).isFalse();
    }

    @Test
    void testAddToken() {
        String token = "testToken";
        member.addToken(token);

        assertThat(member.getAuthToken()).isEqualTo(token);
        assertThat(member.isValid()).isTrue();
    }

    @Test
    void testIsNewWhenCreatedAtIsNull() {
        assertThat(member.isNew()).isTrue();
    }

    @Test
    void testIsNewWhenCreatedAtIsNotNull() {
        // TimeStamp 클래스의 동작을 모의하기 위해 Mockito 사용
        TimeStamp mockTimeStamp = Mockito.mock(TimeStamp.class);
        Mockito.when(mockTimeStamp.getCreatedAt()).thenReturn(LocalDateTime.now());

        // Member 객체의 createdAt 필드에 값을 설정하는 대신 mock 객체의 isNew 메서드를 호출하여 테스트
        Member memberSpy = Mockito.spy(member);
        Mockito.doReturn(mockTimeStamp.getCreatedAt()).when(memberSpy).getCreatedAt();

        assertThat(memberSpy.isNew()).isFalse();
    }
}
