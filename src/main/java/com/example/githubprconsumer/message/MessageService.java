package com.example.githubprconsumer.message;

import com.example.githubprconsumer.messenger.MessengerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageTemplateRepository messageTemplateRepository;

    private static final String NO_COLLABORATOR = "PR 할당자가 없어요. 깃허브 Collaborator 를 추가 한 후 다시 시도 해 주세요";

    public void updateMessage(Long messengerId, String template){
        MessageTemplate messageTemplate = messageTemplateRepository.findByMessengerId(messengerId)
                .orElseThrow(MessengerException.MessengerNotFoundException::new);

        // 응답이 필요할수도?
        messageTemplate.updateMessage(template);
    }

    public void deleteMessage(Long messengerId){
        messageTemplateRepository.deleteAllByMessengerId(messengerId);
    }

    public MessageTemplate save(Long messengerId){
        MessageTemplate messageTemplate = new MessageTemplate(messengerId);
        return messageTemplateRepository.save(messageTemplate);
    }

    // GithubPrResponse 를 주는 쪽, 그리고 assignee 를 주는 쪽을 호출하기로 한다. 혹은 호출당하기로 한다.
    public String getMessage(Long messengerId, GithubPRResponse githubPRResponse, List<String> assigneeLogins){
        if (assigneeLogins.isEmpty()){
            return NO_COLLABORATOR;
        }

        DefaultMessage defaultMessage = githubPRResponse.toDefaultMessage(assigneeLogins);
        MessageTemplate messageTemplate = messageTemplateRepository
                .findByMessengerId(messengerId)
                .orElseGet(() -> save(messengerId));

        String template = messageTemplate.getTemplate();
        return defaultMessage.mergeMessage(template);
    }
}
