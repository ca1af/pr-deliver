package com.example.githubprconsumer.github.application;

import com.example.githubprconsumer.github.application.dto.BotRemoveEvent;
import com.example.githubprconsumer.github.application.dto.GithubCollaboratorInfo;
import com.example.githubprconsumer.github.application.dto.GithubInvitationsInfo;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryInfo;
import com.example.githubprconsumer.github.application.dto.InviterInfo;
import com.example.githubprconsumer.github.domain.CollaboratorException;
import com.example.githubprconsumer.github.domain.GithubBotException;
import com.example.githubprconsumer.member.application.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
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


    @Test
    @DisplayName("봇 계정 초대가 완료되지 않으면 예외가 발생한다.")
    void testCheckAndApproveInvitations_Fail() {
        // given - member
        String login = "login";
        memberService.createIfNotExist(login);

        // given - data
        String fullName = "sample/repository";
        GithubRepositoryInfo repositoryInfo = new GithubRepositoryInfo(99919L, fullName);
        InviterInfo inviterInfo = new InviterInfo(9191L, login);
        GithubInvitationsInfo invitation = new GithubInvitationsInfo(1, repositoryInfo, "write", inviterInfo);
        List<GithubInvitationsInfo> invitations = List.of(invitation);

        when(githubApiService.fetchInvitations()).thenReturn(invitations);
        doNothing().when(githubApiService).approveInvitation(invitation.id());

        assertThatThrownBy(() -> githubBotService.checkAndApproveInvitations(fullName))
                .isInstanceOf(CollaboratorException.BotHasNotInvitedException.class);
    }

    @Test
    @DisplayName("초대가 성공적으로 승인된다.")
    void testCheckAndApproveInvitations_Success() {
        //given
        String fullName = "sample/repository";
        GithubRepositoryInfo repositoryInfo = new GithubRepositoryInfo(99919L, fullName);
        String login = "login";
        InviterInfo inviterInfo = new InviterInfo(9191L, login);
        GithubInvitationsInfo invitation = new GithubInvitationsInfo(1, repositoryInfo, "write", inviterInfo);
        List<GithubInvitationsInfo> invitations = List.of(invitation);
        memberService.createIfNotExist(login);

        when(githubApiService.fetchInvitations()).thenReturn(invitations);
        GithubCollaboratorInfo githubCollaboratorInfo = new GithubCollaboratorInfo("calafs-review-bot", "foo", "bar");
        when(githubApiService.getCollaborators(fullName)).thenReturn(List.of(githubCollaboratorInfo));
        doNothing().when(githubApiService).approveInvitation(invitation.id());

        // when
        githubBotService.checkAndApproveInvitations(fullName);

        // then
        verify(githubApiService, times(1)).approveInvitation(invitation.id());
        verify(githubApiService, times(1)).getCollaborators(fullName);
    }

    @Test
    @DisplayName("해당 레포지토리에 초대되지 않은 경우 예외가 발생한다.")
    void testCheckAndApproveInvitations_NotInvited() {
        String fullName = "non-existent/repository";
        GithubRepositoryInfo repositoryInfo = new GithubRepositoryInfo(99919L, "sample/repository");
        InviterInfo inviterInfo = new InviterInfo(9191L, "write");
        GithubInvitationsInfo githubInvitationsInfo = new GithubInvitationsInfo(1, repositoryInfo, "write", inviterInfo);
        List<GithubInvitationsInfo> invitations = List.of(githubInvitationsInfo);

        when(githubApiService.fetchInvitations()).thenReturn(invitations);

        assertThrows(GithubBotException.NotInvitedException.class, () -> githubBotService.checkAndApproveInvitations(fullName));
    }

    @Test
    @DisplayName("올바르지 않은 권한이 주어진 경우 예외가 발생한다.")
    void testApproveInvitation_InvalidPermission() {
        String fullName = "fullName";
        GithubRepositoryInfo repositoryInfo = new GithubRepositoryInfo(99919L, fullName);
        InviterInfo inviterInfo = new InviterInfo(9191L, "write");
        GithubInvitationsInfo githubInvitationsInfo = new GithubInvitationsInfo(1, repositoryInfo, "read", inviterInfo);
        when(githubApiService.fetchInvitations()).thenReturn(List.of(githubInvitationsInfo));

        assertThrows(GithubBotException.BadAuthorityGrantedException.class,
                () -> githubBotService.checkAndApproveInvitations(fullName));
    }

    @Test
    @DisplayName("레포지토리에서 봇 계정이 성공적으로 제거된다.")
    void testRemoveSelfFromRepository_Success() {
        String fullName = "sample/repository";
        BotRemoveEvent event = new BotRemoveEvent(fullName);

        doNothing().when(githubApiService).removeSelfFromRepository(fullName);

        githubBotService.removeSelfFromRepository(event);

        verify(githubApiService, times(1)).removeSelfFromRepository(fullName);
    }
}
