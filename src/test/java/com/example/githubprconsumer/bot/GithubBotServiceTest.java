package com.example.githubprconsumer.bot;

import com.example.githubprconsumer.bot.api.GithubApiService;
import com.example.githubprconsumer.bot.event.InvalidPermissionEvent;
import com.example.githubprconsumer.github.GithubRepositoryService;
import com.example.githubprconsumer.member.Member;
import com.example.githubprconsumer.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GithubBotServiceTest {

    @Mock
    private GithubApiService githubApiService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GithubBotService githubBotService;

    @Mock
    private MemberService memberService;

    @Mock
    private GithubRepositoryService githubRepositoryService;

    private String login;

    private GithubInvitationsInfo validInvitation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Given
        login = "inviter-login";
        GithubRepositoryInfo githubRepositoryInfo = new GithubRepositoryInfo(1L, "full/name");
        InviterInfo inviterInfo = new InviterInfo(1L, login);
        validInvitation = new GithubInvitationsInfo(1, githubRepositoryInfo, "write", inviterInfo);
    }

    @Test
    void ifEmptyDoNothing() {
        // given
        when(githubApiService.fetchInvitations()).thenReturn(List.of());

        // when
        githubBotService.checkAndApproveInvitations();
        ArgumentCaptor<InvalidPermissionEvent> eventCaptor = ArgumentCaptor.forClass(InvalidPermissionEvent.class);

        // then
        verify(eventPublisher, never()).publishEvent(eventCaptor.capture());
        verify(githubApiService, never()).approveInvitation(any());
    }

    @Test
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
    void checkAndApproveInvitations_ShouldPublishEventForInvalidInvitations() {
        // Given
        GithubInvitationsInfo invalidInvitation = mock(GithubInvitationsInfo.class);
        when(invalidInvitation.permissions()).thenReturn("read");
        when(invalidInvitation.inviterInfo()).thenReturn(new InviterInfo(1L, "inviter-login"));
        when(githubApiService.fetchInvitations()).thenReturn(List.of(invalidInvitation));

        // When
        githubBotService.checkAndApproveInvitations();

        // Then
        ArgumentCaptor<InvalidPermissionEvent> eventCaptor = ArgumentCaptor.forClass(InvalidPermissionEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        InvalidPermissionEvent publishedEvent = eventCaptor.getValue();
        assertEquals(invalidInvitation.id(), publishedEvent.id());
        assertEquals("inviter-login", publishedEvent.login());
    }
}