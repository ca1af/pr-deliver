package com.example.githubprconsumer.bot.api;

import lombok.Getter;

@Getter
public enum GithubBotApiUrl {
    REPOSITORY_INVITATIONS("https://api.github.com/user/repository_invitations"),
    INVITATION_APPROVE("https://api.github.com/user/repository_invitations/:invitationId"),
    ;

    private final String url;

    GithubBotApiUrl(String url) {
        this.url = url;
    }

    public static String getInvitationApproveUrl(Integer invitationNumber) {
        return INVITATION_APPROVE.url.replace(":invitationId", String.valueOf(invitationNumber));
    }
}