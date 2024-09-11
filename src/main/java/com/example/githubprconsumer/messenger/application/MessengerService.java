package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.global.application.EncryptService;
import com.example.githubprconsumer.message.application.MessageService;
import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.example.githubprconsumer.messenger.domain.Messenger;
import com.example.githubprconsumer.messenger.domain.MessengerException;
import com.example.githubprconsumer.messenger.domain.MessengerJpaRepository;
import com.example.githubprconsumer.messenger.domain.MessengerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessengerService {

    private static final String MESSENGER_ADDED = """
            아래 URL을 클릭하시면 메신저 등록이 완료됩니다!
            """;

    private static final String SERVICE_URL = "https://my-service/webhooks/";


    private final MessageService messageService;

    private final MessengerJpaRepository messengerJpaRepository;

    private final EncryptService encryptService;

    // TODO : 여기서 List<MessengerService(이름은 생각해보자)> 를 주입받고, 디스코드 뿐만 아니라 모든 메신저 서비스를 사용할 수 있도록 변경한다.
    private final DiscordMessageService discordMessageService;

    @Transactional
    public void addNewMessenger(MessengerAddRequestDto messengerAddRequestDto){
        Long repositoryId = messengerAddRequestDto.repositoryId();
        MessengerType messengerType = messengerAddRequestDto.messengerType();
        String webhookUrl = messengerAddRequestDto.webhookUrl();

        Optional<Messenger> messengerOptional = messengerJpaRepository.findByRepositoryIdAndMessengerType(repositoryId, messengerType);
        if (messengerOptional.isPresent()){
            throw new MessengerException.DuplicatedMessengerException(messengerType);
        }

        String encryptedWebhookUrl = encryptService.encrypt(webhookUrl);
        Messenger messenger = new Messenger(repositoryId, messengerType, encryptedWebhookUrl);
        messengerJpaRepository.save(messenger);
        discordMessageService.sendMessage(webhookUrl, MESSENGER_ADDED + SERVICE_URL + encryptedWebhookUrl);
    }

    public void activateMessenger(String encodedWebhookUrl){
        Messenger messenger = messengerJpaRepository.findByWebhookUrl(encodedWebhookUrl).orElseThrow(
                () -> new MessengerException.MessengerNotFoundException(encodedWebhookUrl)
        );

        messenger.activate();
    }

    public void sendMessage(Long githubRepositoryId, GithubPRResponse githubPRResponse, List<String> assigneeLogins){
        List<Messenger> messengerList = messengerJpaRepository.findAllByRepositoryId(githubRepositoryId);
        if (messengerList.isEmpty()){
            throw new MessengerException.MessengerNotFoundException();
        }

        messengerList.forEach(messenger -> {
            String defaultMessage = messageService.getMessage(messenger.getId(), githubPRResponse, assigneeLogins);
            String webhookUrl = encryptService.decrypt(messenger.getWebhookUrl());
            discordMessageService.sendMessage(webhookUrl, defaultMessage);
        });
    }

    public void deleteMessenger(Long messengerId){
        messengerJpaRepository.deleteById(messengerId);
        messageService.deleteAllMessagesByMessengerId(messengerId);
    }

    public void deleteAllByRepositoryId(Long repositoryId){
        List<Messenger> messengerList = messengerJpaRepository.findAllByRepositoryId(repositoryId);
        messengerList.forEach(each -> deleteMessenger(each.getId()));
        messengerJpaRepository.deleteAllByRepositoryId(repositoryId);
    }
}
