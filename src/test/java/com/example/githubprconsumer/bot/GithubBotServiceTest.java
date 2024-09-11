package com.example.githubprconsumer.bot;

import com.example.githubprconsumer.github.domain.GithubBotException;
import com.example.githubprconsumer.github.application.GithubBotService;
import com.example.githubprconsumer.github.application.dto.GithubInvitationsInfo;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryInfo;
import com.example.githubprconsumer.github.application.dto.InviterInfo;
import com.example.githubprconsumer.github.application.GithubApiService;
import com.example.githubprconsumer.github.application.GithubRepositoryService;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryAddRequestDto;
import com.example.githubprconsumer.github.application.dto.BotRemoveEvent;
import com.example.githubprconsumer.member.domain.Member;
import com.example.githubprconsumer.member.application.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class GithubBotServiceTest {

    @Autowired
    private GithubBotService githubBotService;

    @MockBean
    private GithubApiService githubApiService;

    @Autowired
    private MemberService memberService;

    @MockBean
    private GithubRepositoryService githubRepositoryService;

    private GithubInvitationsInfo validInvitation;

    private String login;

    private Member validMember;

    @BeforeEach
    void setUp() {
        login = "inviter-login";
        GithubRepositoryInfo githubRepositoryInfo = new GithubRepositoryInfo(1L, "full/name");
        InviterInfo inviterInfo = new InviterInfo(1L, login);
        validInvitation = new GithubInvitationsInfo(1, githubRepositoryInfo, "write", inviterInfo);
        validMember = memberService.createIfNotExist(login);
    }

    @Test
    @DisplayName("깃허브 봇을 Collaborator 로 초대를 하지 않은 상태로 요청하면 예외가 발생한다.")
    void whenNotInvitedBot_thenThrowException() {
        // Given
        GithubInvitationsInfo invalidInvitation = new GithubInvitationsInfo(2, new GithubRepositoryInfo(2L, "invalid/repo"), "read", new InviterInfo(2L, login));

        // Then
        assertThatThrownBy(() -> githubBotService.checkAndApproveInvitations(invalidInvitation.githubRepositoryInfo().fullName()))
                .isInstanceOf(GithubBotException.NotInvitedException.class);
    }

    @Test
    @DisplayName("유효하지 않은 권한의 초대가 있을 경우 예외를 발생시킨다..")
    void checkAndApproveInvitations_ShouldPublishEventForInvalidInvitation() {
        // Given
        GithubInvitationsInfo invalidInvitation = new GithubInvitationsInfo(2, new GithubRepositoryInfo(2L, "invalid/repo"), "read", new InviterInfo(2L, login));
        when(githubApiService.fetchInvitations()).thenReturn(List.of(invalidInvitation));

        // Then
        assertThatThrownBy(() -> githubBotService.checkAndApproveInvitations(invalidInvitation.githubRepositoryInfo().fullName()))
                .isInstanceOf(GithubBotException.BadAuthorityGrantedException.class);
    }

    @Test
    @DisplayName("초대 목록이 비어 있으면 아무 작업도 수행하지 않는다.")
    void ifEmptyDoNothing() {
        // given
        when(githubApiService.fetchInvitations()).thenReturn(List.of());

        // when
        assertThatThrownBy(() -> githubBotService.checkAndApproveInvitations(validInvitation.githubRepositoryInfo().fullName()))
                .isInstanceOf(GithubBotException.NotInvitedException.class);

        // then
        verify(githubApiService, never()).approveInvitation(any());
    }

    @Test
    @DisplayName("유효한 초대가 있을 경우 초대를 승인한다.")
    void checkAndApproveInvitations_ShouldApproveValidInvitation() {
        // Given
        when(githubApiService.fetchInvitations()).thenReturn(List.of(validInvitation));

        // When
        githubBotService.checkAndApproveInvitations(validInvitation.githubRepositoryInfo().fullName());

        // Then
        verify(githubApiService, times(1)).approveInvitation(validInvitation.id());
        verify(githubRepositoryService, times(1)).addGithubRepository(any(GithubRepositoryAddRequestDto.class));
    }

    @Test
    @DisplayName("유효한 사용자로부터 초대를 받을 경우 레포지토리를 등록한다.")
    void approveInvitation_ShouldRegisterRepositoryWhenMemberExists() {
        // When
        githubBotService.approveInvitation(validInvitation);

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
