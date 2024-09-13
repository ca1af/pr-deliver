package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.messenger.application.dto.SlackMessage;
import com.example.githubprconsumer.messenger.domain.MessengerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
@Log4j2
public class SlackMessageService implements MessageSendService {

    private final RestClient restClient;

    @Override
    public void sendMessage(String webhookUrl, String messageContent) {
        try {
            SlackMessage message = new SlackMessage(messageContent);

            restClient.post()
                    .uri(webhookUrl)
                    .headers(headers -> headers.set(HttpHeaders.CONTENT_TYPE, "application/json"))
                    .body(message)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully sent message to Slack webhook: {}", webhookUrl);
        } catch (RestClientException e) {
            log.error("Failed to send message to Slack webhook {}: {}", webhookUrl, e.getMessage());
            throw e;
        }
    }

    @Override
    public MessengerType getMessengerType() {
        return MessengerType.SLACK;
    }
}

