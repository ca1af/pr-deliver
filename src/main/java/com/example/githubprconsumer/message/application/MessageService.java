package com.example.githubprconsumer.message.application;

import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.example.githubprconsumer.message.domain.Message;
import com.example.githubprconsumer.message.domain.MessageRepository;
import com.example.githubprconsumer.messenger.MessengerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

    private static final String NO_COLLABORATOR = "PR 할당자가 없어요. 깃허브 Collaborator 를 추가 한 후 다시 시도 해 주세요";

    public void updateMessage(Long messengerId, String template){
        Message message = messageRepository.findByMessengerId(messengerId)
                .orElseThrow(MessengerException.MessengerNotFoundException::new);

        // 응답이 필요할수도?
        message.updateMessage(template);
    }

    public void deleteAllMessagesByMessengerId(Long messengerId){
        messageRepository.deleteAllByMessengerId(messengerId);
    }

    public Message save(Long messengerId){
        Message message = new Message(messengerId);
        return messageRepository.save(message);
    }

    // GithubPrResponse 를 주는 쪽, 그리고 assignee 를 주는 쪽을 호출하기로 한다. 혹은 호출당하기로 한다.
    public String getMessage(Long messengerId, GithubPRResponse githubPRResponse, List<String> assigneeLogins){
        if (assigneeLogins.isEmpty()){
            return NO_COLLABORATOR;
        }

        Message message = messageRepository
                .findByMessengerId(messengerId)
                .orElseGet(() -> save(messengerId));

        return message.mergeMessage(githubPRResponse.prTitle(), githubPRResponse.prLink(), githubPRResponse.prAuthor(), assigneeLogins);
    }
}
