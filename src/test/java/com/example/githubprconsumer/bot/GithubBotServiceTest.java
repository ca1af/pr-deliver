package com.example.githubprconsumer.bot;

import com.example.githubprconsumer.bot.api.GithubApiService;
import com.example.githubprconsumer.bot.event.InvalidPermissionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkAndApproveInvitations_ShouldApproveValidInvitations() {
        // Given
        GithubInvitationsInfo validInvitation = mock(GithubInvitationsInfo.class);
        when(validInvitation.permissions()).thenReturn("write");
        when(githubApiService.fetchInvitations()).thenReturn(List.of(validInvitation));
        when(validInvitation.githubRepositoryInfo()).thenReturn(mock());

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