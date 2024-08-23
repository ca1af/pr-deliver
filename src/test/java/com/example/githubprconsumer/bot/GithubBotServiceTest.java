package com.example.githubprconsumer.bot;

import com.example.githubprconsumer.bot.api.GithubApiService;
import com.example.githubprconsumer.bot.event.InvalidPermissionEvent;
import com.example.githubprconsumer.github.GithubRepositoryService;
import com.example.githubprconsumer.github.dto.GithubRepositoryAddRequestDto;
import com.example.githubprconsumer.github.event.BotRemoveEvent;
import com.example.githubprconsumer.member.Member;
import com.example.githubprconsumer.member.MemberService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@RecordApplicationEvents
class GithubBotServiceTest {

    @Autowired
    private GithubBotService githubBotService;

    @MockBean
    private GithubApiService githubApiService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private GithubRepositoryService githubRepositoryService;

    @Autowired
    private ApplicationEvents applicationEvents;

    private GithubInvitationsInfo validInvitation;
    private String login;


    @BeforeEach
    void setUp() {
        login = "inviter-login";
        GithubRepositoryInfo githubRepositoryInfo = new GithubRepositoryInfo(1L, "full/name");
        InviterInfo inviterInfo = new InviterInfo(1L, login);
        validInvitation = new GithubInvitationsInfo(1, githubRepositoryInfo, "write", inviterInfo);
    }

    @Test
    @DisplayName("유효하지 않은 권한의 초대가 있을 경우 이벤트를 발행한다.")
    void checkAndApproveInvitations_ShouldPublishEventForInvalidInvitations() {
        // Given
        GithubInvitationsInfo invalidInvitation = new GithubInvitationsInfo(2, new GithubRepositoryInfo(2L, "invalid/repo"), "read", new InviterInfo(2L, "inviter-login"));
        when(githubApiService.fetchInvitations()).thenReturn(List.of(invalidInvitation));

        // When
        githubBotService.checkAndApproveInvitations();
        InvalidPermissionEvent event = applicationEvents.stream(InvalidPermissionEvent.class).findFirst().orElse(null);

        // Then
        SoftAssertions.assertSoftly(softly -> {
            assertThat(event).isNotNull();
            softly.assertThat(event.id()).isEqualTo(invalidInvitation.id());
            softly.assertThat(event.login()).isEqualTo("inviter-login");
        });
    }

    @Test
    @DisplayName("등록되지 않은 사용자로부터 초대를 받을 경우 이벤트를 발행한다.")
    void approveInvitations_ShouldPublishEventWhenMemberNotExists() {
        // Given
        when(memberService.existsByLogin(login)).thenReturn(false);

        // When
        githubBotService.approveInvitations(validInvitation);
        InvalidPermissionEvent event = applicationEvents.stream(InvalidPermissionEvent.class).findFirst().orElse(null);

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(event).isNotNull();
            assert event != null;
            softly.assertThat(event.id()).isEqualTo(validInvitation.id());
            softly.assertThat(event.login()).isEqualTo(login);
        });
    }

    @Test
    @DisplayName("초대 목록이 비어 있으면 아무 작업도 수행하지 않는다.")
    void ifEmptyDoNothing() {
        // given
        when(githubApiService.fetchInvitations()).thenReturn(List.of());

        // when
        githubBotService.checkAndApproveInvitations();

        // then
        verify(githubApiService, never()).approveInvitation(any());
    }

    @Test
    @DisplayName("유효한 초대가 있을 경우 초대를 승인한다.")
    void checkAndApproveInvitations_ShouldApproveValidInvitations() {
        // Given
        when(memberService.existsByLogin(login)).thenReturn(true);
        when(githubApiService.fetchInvitations()).thenReturn(List.of(validInvitation));
        when(memberService.getMemberByLogin(login)).thenReturn(mock(Member.class));

        // When
        githubBotService.checkAndApproveInvitations();

        // Then
        verify(githubApiService, times(1)).approveInvitation(validInvitation.id());
    }

    @Test
    @DisplayName("유효한 사용자로부터 초대를 받을 경우 레포지토리를 등록한다.")
    void approveInvitations_ShouldRegisterRepositoryWhenMemberExists() {
        // Given
        Member member = new Member(login);
        when(memberService.existsByLogin(login)).thenReturn(true);
        when(memberService.getMemberByLogin(login)).thenReturn(member);

        // When
        githubBotService.approveInvitations(validInvitation);

        // Then
        verify(githubApiService, times(1)).approveInvitation(validInvitation.id());
        verify(githubRepositoryService, times(1)).addGithubRepository(any(GithubRepositoryAddRequestDto.class));
    }

    @Test
    @DisplayName("봇 제거 이벤트가 발생하면 저장소에서 자신을 제거한다.")
    void removeSelfFromRepository_ShouldRemoveBotFromRepository() {
        // Given
        String fullName = "test/repository";
        BotRemoveEvent botRemoveEvent = new BotRemoveEvent(fullName);

        // When
        githubBotService.removeSelfFromRepository(botRemoveEvent);

        // Then
        verify(githubApiService, times(1)).removeSelfFromRepository(fullName);
    }
}
