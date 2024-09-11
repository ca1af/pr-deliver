package com.example.githubprconsumer.messenger.domain;

import com.example.githubprconsumer.global.exception.BadRequestException;
import com.example.githubprconsumer.global.exception.NotFoundException;

public class MessengerException extends RuntimeException {
    public static class MessengerNotFoundException extends NotFoundException {

        private static final String MESSENGER_NOT_FOUND = "등록된 메신저가 없습니다.";

        private static final String WEBHOOK_URL = "Webhook URL : ";

        public MessengerNotFoundException() {
            super(MESSENGER_NOT_FOUND);
        }

        public MessengerNotFoundException(String webhookUrl) {
            super(MESSENGER_NOT_FOUND + WEBHOOK_URL + webhookUrl);
        }
    }

    public static class DuplicatedMessengerException extends BadRequestException {

        private static final String ALREADY_ADDED_THIS_MESSENGER = " : 해당 메신저는 이미 등록되었습니다.";

        public DuplicatedMessengerException(MessengerType messengerType) {
            super(messengerType + ALREADY_ADDED_THIS_MESSENGER);
        }
    }
}
