package com.example.githubprconsumer.member.domain;

import com.example.githubprconsumer.global.exception.NotFoundException;

public class MemberException extends RuntimeException {

    public static class MemberNotFoundException extends NotFoundException {
        private static final String MESSAGE = "회원을 찾을 수 없습니다. 회원 이메일 : %s";
        public MemberNotFoundException(String githubEmail) {
            super(String.format(MESSAGE, githubEmail));
        }
    }
}
