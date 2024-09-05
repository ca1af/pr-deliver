package com.example.githubprconsumer.message.domain;

import com.example.githubprconsumer.global.BadRequestException;

public class MessageTemplateException extends RuntimeException {

    public MessageTemplateException(String message) {
        super(message);
    }

    public static class MissingPlaceholderTemplateException extends BadRequestException {

        private static final String DEFAULT_MESSAGE = "필수 메시지 입력값이 누락되었습니다. 누락된 값 : ";

        public MissingPlaceholderTemplateException(String message) {
            super(DEFAULT_MESSAGE + message);
        }
    }
}
