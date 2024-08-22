package com.example.githubprconsumer.messenger;

import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.example.githubprconsumer.message.application.MessageService;
import com.example.githubprconsumer.messenger.discord.DiscordMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessengerService {

    private final MessageService messageService;

    private final MessengerJpaRepository messengerJpaRepository;

    private final DiscordMessageService discordMessageService;

    public void addNewMessenger(MessengerAddRequestDto messengerAddRequestDto){
        Long repositoryId = messengerAddRequestDto.repositoryId();
        MessengerType messengerType = messengerAddRequestDto.messengerType();
        String webhookUrl = messengerAddRequestDto.webhookUrl();

        Optional<Messenger> messengerOptional = messengerJpaRepository.findByRepositoryIdAndMessengerType(repositoryId, messengerType);
        if (messengerOptional.isPresent()){
            throw new MessengerException.DuplicatedMessengerException(messengerType);
        }

        Messenger messenger = new Messenger(repositoryId, messengerType, webhookUrl);
        messengerJpaRepository.save(messenger);
    }

    public void sendMessage(Long githubRepositoryId, GithubPRResponse githubPRResponse, List<String> assigneeLogins){
        List<Messenger> messengerList = messengerJpaRepository.findAllByRepositoryId(githubRepositoryId);
        if (messengerList.isEmpty()){
            throw new MessengerException.MessengerNotFoundException();
        }

        messengerList.forEach(messenger -> {
            String defaultMessage = messageService.getMessage(messenger.getId(), githubPRResponse, assigneeLogins);
            discordMessageService.sendMessage(messenger.getWebhookUrl(), defaultMessage);
        });
    }

    public void deleteMessenger(Long messengerId){
        messengerJpaRepository.deleteById(messengerId);
        messageService.deleteMessage(messengerId);
    }

    public void deleteAllByRepositoryId(Long repositoryId){
        List<Messenger> messengerList = messengerJpaRepository.findAllByRepositoryId(repositoryId);
        messengerList.forEach(each -> deleteMessenger(each.getId()));
        messengerJpaRepository.deleteAllByRepositoryId(repositoryId);
    }
}
