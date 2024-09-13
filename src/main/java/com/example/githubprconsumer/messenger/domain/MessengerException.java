package com.example.githubprconsumer.messenger.domain;

import com.example.githubprconsumer.global.exception.BadRequestException;
import com.example.githubprconsumer.global.exception.NotFoundException;

public class MessengerException extends RuntimeException {
    public static class MessengerNotFoundException extends NotFoundException {

        private static final String MESSENGER_NOT_FOUND = "등록된 메신저가 없습니다.";

        private static final String WEBHOOK_URL = "Webhook URL : ";

        private static final String MESSENGER_ID = "messengerId : ";

        public MessengerNotFoundException() {
            super(MESSENGER_NOT_FOUND);
        }

        public MessengerNotFoundException(String webhookUrl) {
            super(MESSENGER_NOT_FOUND + WEBHOOK_URL + webhookUrl);
        }

        public MessengerNotFoundException(Long messengerId) {
            super(MESSENGER_NOT_FOUND + MESSENGER_ID + messengerId);
        }
    }

    public static class DuplicatedMessengerException extends BadRequestException {

        private static final String ALREADY_ADDED_THIS_MESSENGER = " : 해당 메신저는 이미 등록되었습니다.";

        public DuplicatedMessengerException(MessengerType messengerType) {
            super(messengerType + ALREADY_ADDED_THIS_MESSENGER);
        }
    }

    public static class NotMyMessengerException extends BadRequestException {

        private static final String NOT_MY_MESSENGER = "등록자만 삭제 할 수 있습니다. 등록자 : %s";

        public NotMyMessengerException(String login) {
            super(String.format(NOT_MY_MESSENGER, login));
        }
    }
}
