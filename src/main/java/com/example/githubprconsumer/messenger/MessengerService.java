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

    // TODO : 여기서 List<MessengerService(이름은 생각해보자)> 를 주입받고, 디스코드 뿐만 아니라 모든 메신저 서비스를 사용할 수 있도록 변경한다.
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
