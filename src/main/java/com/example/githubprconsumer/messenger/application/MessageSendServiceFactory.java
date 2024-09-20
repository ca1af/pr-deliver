package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.messenger.domain.MessengerType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageSendServiceFactory {

    private Map<MessengerType, MessageSendService> messageSendServiceMap = new HashMap<>();

    public MessageSendServiceFactory(List<MessageSendService> messageSendServices) {
        messageSendServices.forEach(
                each -> messageSendServiceMap.put(each.getMessengerType(), each)
        );
    }

    public MessageSendService getMessageSendService(MessengerType messengerType) {
        return messageSendServiceMap.get(messengerType);
    }
}
