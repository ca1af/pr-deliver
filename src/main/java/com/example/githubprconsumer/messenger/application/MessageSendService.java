package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.messenger.domain.MessengerType;

public interface MessageSendService {

    void sendMessage(String webhookUrl, String messageContent);

    MessengerType getMessengerType();

}
