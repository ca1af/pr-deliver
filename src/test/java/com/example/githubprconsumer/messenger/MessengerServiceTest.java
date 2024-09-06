package com.example.githubprconsumer.messenger;

import com.example.githubprconsumer.global.EncryptService;
import com.example.githubprconsumer.message.application.MessageService;
import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.example.githubprconsumer.messenger.discord.DiscordMessageService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class MessengerServiceTest {

    @Autowired
    private MessengerJpaRepository messengerJpaRepository;

    @Autowired
    private MessengerService messengerService;

    @Autowired
    private EncryptService encryptService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private DiscordMessageService discordMessageService;

    @Test
    @DisplayName("새로운 메신저를 성공적으로 추가하고 메시지를 전송한다.")
    void testAddNewMessenger_Success() {
        // Given
        Long repositoryId = 1L;
        MessengerType messengerType = MessengerType.DISCORD;
        String webhookUrl = "https://discord.com/api/webhooks/...";
        String encryptedWebhookUrl = encryptService.encrypt(webhookUrl);

        MessengerAddRequestDto requestDto = new MessengerAddRequestDto(repositoryId, messengerType, webhookUrl);

        // When
        messengerService.addNewMessenger(requestDto);

        // Then
        verify(discordMessageService, times(1)).sendMessage(eq(webhookUrl), contains(encryptedWebhookUrl)); // 메시지가 전송되는지 확인
    }

    @Test
    @DisplayName("같은 레포지토리에 중복된 메신저 (EX : Discord 가 이미 존재하는데 Discord 추가) 를 추가하면 예외가 발생한다.")
    void testAddNewMessenger_DuplicatedMessenger() {
        // Given
        Long repositoryId = 1L;
        MessengerType messengerType = MessengerType.DISCORD;
        String webhookUrl = "https://discord.com/api/webhooks/...";
        Messenger messenger = new Messenger(repositoryId, messengerType, webhookUrl);
        messengerJpaRepository.save(messenger);

        MessengerAddRequestDto requestDto = new MessengerAddRequestDto(repositoryId, messengerType, webhookUrl);

        // When & Then
        assertThatThrownBy(() -> messengerService.addNewMessenger(requestDto))
                .isInstanceOf(MessengerException.DuplicatedMessengerException.class);
    }

    @Test
    @DisplayName("메신저를 활성화하는 데 성공한다.")
    void testActivateMessenger_Success() {
        // Given
        Long repositoryId = 1L;
        MessengerType messengerType = MessengerType.DISCORD;
        String webhookUrl = "https://discord.com/api/webhooks/...";
        String encryptedWebhookUrl = encryptService.encrypt(webhookUrl);

        Messenger messenger = new Messenger(repositoryId, messengerType, encryptedWebhookUrl);
        messengerJpaRepository.save(messenger);

        // When
        messengerService.activateMessenger(encryptedWebhookUrl);

        // Then
        Messenger activatedMessenger = messengerJpaRepository.findByWebhookUrl(encryptedWebhookUrl)
                .orElseThrow(() -> new MessengerException.MessengerNotFoundException(encryptedWebhookUrl));
        assertThat(activatedMessenger.isActive()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 메신저를 활성화하려 하면 예외가 발생한다.")
    void testActivateMessenger_MessengerNotFound() {
        // Given
        String invalidWebhookUrl = "invalid_webhook_url";

        // When & Then
        assertThatThrownBy(() -> messengerService.activateMessenger(invalidWebhookUrl))
                .isInstanceOf(MessengerException.MessengerNotFoundException.class);
    }

    @Test
    @DisplayName("메시지를 보낼 때 메신저 목록이 비워져있다면 예외가 발생한다.")
    void testSendMessage_NoMessengerFound() {
        // Given
        Long repositoryId = 9999999L; // 사용되지 않을 녀석으로 준비한다.
        GithubPRResponse githubPRResponse = new GithubPRResponse(
                "Update README.md",
                "https://github.com/example/repo/pull/1",
                "octocat"
        );
        List<String> assigneeLogins = Collections.singletonList("assignee1");

        // 메신저 목록을 비운다
        messengerJpaRepository.deleteAllByRepositoryId(repositoryId);

        // When & Then
        assertThatThrownBy(() -> messengerService.sendMessage(repositoryId, githubPRResponse, assigneeLogins))
                .isInstanceOf(MessengerException.MessengerNotFoundException.class);
    }

    @Test
    @DisplayName("메시지를 보내는 데 성공한다.")
    void testSendMessage_Success() {
        // Given
        Long repositoryId = 1L;
        MessengerType messengerType = MessengerType.DISCORD;
        String webhookUrl = "https://discord.com/api/webhooks/...";
        String encryptedWebhookUrl = encryptService.encrypt(webhookUrl);
        Messenger messenger = new Messenger(repositoryId, messengerType, encryptedWebhookUrl);
        messengerJpaRepository.save(messenger); // 메신저를 하나 저장한다.

        GithubPRResponse githubPRResponse = new GithubPRResponse(
                "Update README.md",
                "https://github.com/example/repo/pull/1",
                "octocat"
        );

        List<String> assigneeLogins = Collections.singletonList("assignee1");

        String expectedMessage = "Test Message";
        when(messageService.getMessage(anyLong(), eq(githubPRResponse), eq(assigneeLogins))).thenReturn(expectedMessage);
        doNothing().when(discordMessageService).sendMessage(webhookUrl, expectedMessage);

        // When
        messengerService.sendMessage(repositoryId, githubPRResponse, assigneeLogins);

        // Then
        verify(discordMessageService, times(1)).sendMessage(webhookUrl, expectedMessage); // 메시지 전송 호출 검증
    }

    @Test
    @DisplayName("메신저를 삭제하는 데 성공한다.")
    void testDeleteMessenger_Success() {
        // Given
        Long repositoryId = 1L;
        MessengerType messengerType = MessengerType.DISCORD;
        String webhookUrl = "https://discord.com/api/webhooks/...";
        Messenger messenger = new Messenger(repositoryId, messengerType, webhookUrl);
        Messenger savedMessenger = messengerJpaRepository.save(messenger);

        // When
        messengerService.deleteMessenger(savedMessenger.getId());

        // Then
        assertThat(messengerJpaRepository.findById(savedMessenger.getId())).isEmpty();
        verify(messageService, times(1)).deleteAllMessagesByMessengerId(savedMessenger.getId());
    }

    @Test
    @DisplayName("레포지토리 ID로 모든 메신저를 지우는 데 성공한다.")
    void testDeleteAllByRepositoryId_Success() {
        // Given
        Long repositoryId = 1L;
        MessengerType messengerType1 = MessengerType.DISCORD;
        MessengerType messengerType2 = MessengerType.SLACK;
        Messenger messenger1 = new Messenger(repositoryId, messengerType1, "https://discord.com/api/webhooks/...");
        Messenger messenger2 = new Messenger(repositoryId, messengerType2, "https://slack.com/api/webhooks/...");
        messengerJpaRepository.save(messenger1);
        messengerJpaRepository.save(messenger2);

        // When
        messengerService.deleteAllByRepositoryId(repositoryId);

        // Then
        assertThat(messengerJpaRepository.findAllByRepositoryId(repositoryId)).isEmpty();
        verify(messageService, times(2)).deleteAllMessagesByMessengerId(anyLong());
    }
}
