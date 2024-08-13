package com.example.githubprconsumer.bot;

import com.example.githubprconsumer.bot.api.GithubApiService;
import com.example.githubprconsumer.bot.event.InvalidPermissionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class GithubBotService {

    private final GithubApiService githubApiService;

    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRate = 3000)
    public void checkAndApproveInvitations() {
        List<GithubInvitationsInfo> invitations = githubApiService.fetchInvitations();

        // permission 이 write 가 아니면 예외처리
        // inviter 의 login 항목을 통해서 예외 던지기 (알림발송 등)
        invitations.stream()
                .filter(invitation -> !invitation.permissions().contains("write"))
                .findAny()
                .ifPresent(this::sendInvalidPermissionEvent);

        // 초대 목록을 받아온 후, 초대 승인 또는 추가 작업 수행
        invitations.stream()
                .filter(each -> each.permissions().contains("write"))
                .forEach(this::approveInvitations);
    }

    private void sendInvalidPermissionEvent(GithubInvitationsInfo invitation) {
        eventPublisher.publishEvent(new InvalidPermissionEvent(invitation.id(), invitation.inviterInfo().login()));
    }

    public void approveInvitations(GithubInvitationsInfo invitation) {
        log.info("레포지토리 초대를 수락합니다. 레포지토리 풀네임 : {}", invitation.githubRepositoryInfo().fullName());
        githubApiService.approveInvitation(invitation.id());
    }
}