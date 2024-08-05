package com.example.githubprconsumer.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageTemplateRepository messageTemplateRepository;

    public void save(){
        // github repository 객체가 생성되면, 세이브도 작성한다.
    }
    // GithubPrResponse 를 주는 쪽, 그리고 assignee 를 주는 쪽을 호출하기로 한다. 혹은 호출당하기로 한다.
    public String getDefaultMessage(String githubRepositoryId, GithubPRResponse githubPRResponse, String assignee){
        DefaultMessage defaultMessage = githubPRResponse.toDefaultMessage(assignee);
        MessageTemplate messageTemplate = messageTemplateRepository.findByGithubRepositoryId(githubRepositoryId).orElseThrow(
                () -> new MessageTemplateException.MessageTemplateNotFoundException(githubRepositoryId)
        );

        String template = messageTemplate.getTemplate();
        return defaultMessage.mergeMessage(template);
    }
}
