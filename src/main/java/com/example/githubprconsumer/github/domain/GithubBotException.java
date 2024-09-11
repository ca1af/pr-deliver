package com.example.githubprconsumer.github.domain;

import com.example.githubprconsumer.global.exception.BadRequestException;

public class GithubBotException extends RuntimeException{
    public GithubBotException(String message) {
        super(message);
    }

    public static class NotInvitedException extends BadRequestException {

        private static final String NOT_INVITED = "봇 계정에 대한 초대가 완료되지 않았습니다. fullName : %s";

        public NotInvitedException(String fullName) {
            super(String.format(NOT_INVITED, fullName));
        }
    }

    public static class BadAuthorityGrantedException extends BadRequestException {

        private static final String WRITE_PERMISSION_REQUIRED = "Write 권한이 필요합니다. inviterLogin : %s";

        public BadAuthorityGrantedException(String inviterLogin) {
            super(String.format(WRITE_PERMISSION_REQUIRED, inviterLogin));
        }
    }
}
