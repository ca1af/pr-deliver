package com.example.githubprconsumer.message;

import com.example.githubprconsumer.global.BadRequestException;
import com.example.githubprconsumer.global.NotFoundException;

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

    public static class MessageTemplateNotFoundException extends NotFoundException {

        private static final String MESSAGE_TEMPLATE_NOT_FOUND = "메시지 템플릿을 찾을 수 없습니다. 회원 ID : ";

        public MessageTemplateNotFoundException(String memberId) {
            super(MESSAGE_TEMPLATE_NOT_FOUND + memberId);
        }
    }
}
