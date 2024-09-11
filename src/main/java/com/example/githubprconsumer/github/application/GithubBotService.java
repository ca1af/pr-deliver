package com.example.githubprconsumer.github.application;

import com.example.githubprconsumer.github.domain.GithubBotException;
import com.example.githubprconsumer.github.application.dto.GithubInvitationsInfo;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryAddRequestDto;
import com.example.githubprconsumer.github.application.dto.InviterInfo;
import com.example.githubprconsumer.github.application.dto.BotRemoveEvent;
import com.example.githubprconsumer.member.domain.Member;
import com.example.githubprconsumer.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class GithubBotService {

    private final GithubApiService githubApiService;

    private final MemberService memberService;

    private final GithubRepositoryService githubRepositoryService;

    public void checkAndApproveInvitations(String fullName) {
        List<GithubInvitationsInfo> invitations = githubApiService.fetchInvitations();

        GithubInvitationsInfo githubInvitationsInfo = invitations.stream().filter(each -> each.githubRepositoryInfo().fullName().equals(fullName))
                .findAny()
                .orElseThrow(
                        () -> new GithubBotException.NotInvitedException(fullName)
                );
        InviterInfo inviterInfo = githubInvitationsInfo.inviterInfo();

        validateInvitation(inviterInfo.login(), githubInvitationsInfo.permissions());
        approveInvitation(githubInvitationsInfo);
    }

    private void validateInvitation(String inviterLogin, String permissions){
        if (!permissions.contains("write")) {
            throw new GithubBotException.BadAuthorityGrantedException(inviterLogin);
        }
    }

    public void approveInvitation(GithubInvitationsInfo invitation) {
        String inviterLogin = invitation.inviterInfo().login();

        log.info("레포지토리 초대를 수락합니다. 레포지토리 풀네임 : {}", invitation.githubRepositoryInfo().fullName());

        Member member = memberService.getMemberByLogin(inviterLogin);

        // 2. 만약 등록된 사용자라면 레포지토리를 등록한다. 등록자는 inviter 이름으로 한다.
        githubApiService.approveInvitation(invitation.id());
        githubRepositoryService.addGithubRepository(new GithubRepositoryAddRequestDto(member.getLogin(), invitation.githubRepositoryInfo().fullName()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void removeSelfFromRepository(BotRemoveEvent botRemoveEvent){
        githubApiService.removeSelfFromRepository(botRemoveEvent.fullName());
    }
}