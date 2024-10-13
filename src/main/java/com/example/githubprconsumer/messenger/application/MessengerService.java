package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.global.application.EncryptService;
import com.example.githubprconsumer.message.application.MessageService;
import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.example.githubprconsumer.messenger.application.dto.MessengerAddRequestDto;
import com.example.githubprconsumer.messenger.application.dto.MessengerResponseDto;
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
@Transactional
public class MessengerService {

    private static final String MESSENGER_ADDED = """
            아래 URL을 클릭하시면 메신저 등록이 완료됩니다!
            """;

    private static final String SERVICE_URL = "http://localhost:8080/";

    private final MessageService messageService;

    private final MessengerJpaRepository messengerJpaRepository;

    private final EncryptService encryptService;

    private final MessageSendServiceFactory messageSendServiceFactory;

    private final MessengerAliasService messengerAliasService;

    public MessengerResponseDto addNewMessenger(MessengerAddRequestDto messengerAddRequestDto, String login){
        Long repositoryId = messengerAddRequestDto.repositoryId();
        MessengerType messengerType = messengerAddRequestDto.messengerType();
        String webhookUrl = messengerAddRequestDto.webhookUrl();

        Optional<Messenger> messengerOptional = messengerJpaRepository.findByRepositoryIdAndMessengerType(repositoryId, messengerType);
        if (messengerOptional.isPresent()){
            throw new MessengerException.DuplicatedMessengerException(messengerType);
        }

        String encryptedWebhookUrl = encryptService.encrypt(webhookUrl);
        Messenger messenger = new Messenger(repositoryId, messengerType, encryptedWebhookUrl, login);
        messengerJpaRepository.save(messenger);

        MessageSendService messageSendService = messageSendServiceFactory.getMessageSendService(messengerType);
        messageSendService.sendMessage(webhookUrl, MESSENGER_ADDED + SERVICE_URL + encryptedWebhookUrl);

        return MessengerResponseDto.ofNew(messenger);
    }

    public void activateMessenger(String encodedWebhookUrl){
        Messenger messenger = messengerJpaRepository.findByWebhookUrl(encodedWebhookUrl).orElseThrow(
                () -> new MessengerException.MessengerNotFoundException(encodedWebhookUrl)
        );

        messenger.activate();
    }

    public void sendMessage(Long githubRepositoryId, GithubPRResponse githubPRResponse, List<String> collaboratorsLogin){
        List<Messenger> messengerList = messengerJpaRepository.findAllByRepositoryId(githubRepositoryId);
        if (messengerList.isEmpty()){
            throw new MessengerException.MessengerNotFoundException();
        }

        messengerList.forEach(messenger -> {
            List<String> appliedAliases = messengerAliasService.applyMessengerAlias(messenger.getId(), collaboratorsLogin);
            String defaultMessage = messageService.getMessage(messenger.getId(), githubPRResponse, appliedAliases);
            String webhookUrl = encryptService.decrypt(messenger.getWebhookUrl());
            MessageSendService messageSendService = messageSendServiceFactory.getMessageSendService(messenger.getMessengerType());
            messageSendService.sendMessage(webhookUrl, defaultMessage);
        });
    }

    public void deleteMessenger(Long messengerId, String login){
        Messenger messenger = messengerJpaRepository.findById(messengerId).orElseThrow(
                () -> new MessengerException.MessengerNotFoundException(messengerId)
        );

        if (!messenger.isMine(login)){
            throw new MessengerException.NotMyMessengerException(messenger.getLogin());
        }

        messengerJpaRepository.deleteById(messengerId);
        messageService.deleteAllMessagesByMessengerId(messengerId);
    }

    public void deleteAllByRepositoryId(Long repositoryId, String login){
        List<Messenger> messengerList = messengerJpaRepository.findAllByRepositoryId(repositoryId);
        messengerList.forEach(each -> deleteMessenger(each.getId(), login));
        messengerJpaRepository.deleteAllByRepositoryId(repositoryId);
    }
}
