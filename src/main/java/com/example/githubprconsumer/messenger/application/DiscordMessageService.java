package com.example.githubprconsumer.messenger.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
@Log4j2
public class DiscordMessageService {

    private final RestClient restClient;

    public void sendMessage(String webhookUrl, String messageContent) {
        try {
            DiscordMessage message = new DiscordMessage(messageContent);

            restClient.post()
                    .uri(webhookUrl)
                    .headers(headers -> headers.set(HttpHeaders.CONTENT_TYPE, "application/json"))
                    .body(message)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully sent message to Discord webhook: {}", webhookUrl);
        } catch (RestClientException e) {
            log.error("Failed to send message to Discord webhook {}: {}", webhookUrl, e.getMessage());
            throw e;
        }
    }
}
