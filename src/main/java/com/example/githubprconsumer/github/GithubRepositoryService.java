package com.example.githubprconsumer.github;

import com.example.githubprconsumer.collaborator.Collaborator;
import com.example.githubprconsumer.collaborator.CollaboratorException;
import com.example.githubprconsumer.collaborator.CollaboratorService;
import com.example.githubprconsumer.github.dto.GithubRepositoryAddRequestDto;
import com.example.githubprconsumer.github.dto.GithubRepositoryResponseDto;
import com.example.githubprconsumer.github.event.BotRemoveEvent;
import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.example.githubprconsumer.messenger.MessengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubRepositoryService {

    private final GithubRepositoryJpaRepository jpaRepository;

    private final CollaboratorService collaboratorService;

    private final MessengerService messengerService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void addGithubRepository(GithubRepositoryAddRequestDto githubRepositoryAddRequestDto){
        GithubRepository githubRepository = githubRepositoryAddRequestDto.toEntity();
        jpaRepository.save(githubRepository);
        collaboratorService.addCollaborators(githubRepository.getId(), githubRepository.getFullName());
    }

    public GithubRepositoryResponseDto getGithubRepository(Long repositoryId){
        GithubRepository githubRepository = jpaRepository.findById(repositoryId).orElseThrow(
                () -> new GithubRepositoryException.GithubRepositoryNotFoundException(repositoryId)
        );

        return GithubRepositoryResponseDto.of(githubRepository);
    }

    @Transactional
    public void updateAssigneeCount(Long repositoryId, Integer assigneeCount){
        GithubRepository githubRepository = jpaRepository.findById(repositoryId).orElseThrow(
                () -> new GithubRepositoryException.GithubRepositoryNotFoundException(repositoryId)
        );

        Integer collaboratorCount = collaboratorService.getCollaboratorCount(repositoryId);
        if (assigneeCount > collaboratorCount) {
            throw new CollaboratorException.InvalidCollaboratorCountException();
        }

        githubRepository.updateAssigneeCount(assigneeCount);
    }

    public void sendWebhookNotification(String webhookUrl, GithubPRResponse githubPRResponse){
        GithubRepository githubRepository = jpaRepository.findByWebhookUrl(webhookUrl).orElseThrow(
                () -> new GithubRepositoryException.GithubRepositoryNotFoundException(webhookUrl)
        );

        List<Collaborator> collaborators = collaboratorService.getCollaborators(githubRepository.getId(), githubPRResponse.prAuthor());
        List<String> assigneeLogins = collaborators.stream().map(Collaborator::getLogin).toList();

        // 여기서 노티를 보낸다.
        messengerService.sendMessage(githubRepository.getId(), githubPRResponse, assigneeLogins);
    }

    @Transactional
    public void deleteGithubRepository(Long repositoryId){
        messengerService.deleteAllByRepositoryId(repositoryId);
        GithubRepository githubRepository = jpaRepository.findById(repositoryId)
                .orElseThrow(
                        () -> new GithubRepositoryException.GithubRepositoryNotFoundException(repositoryId)
                );
        String fullName = githubRepository.getFullName();
        jpaRepository.delete(githubRepository);
        applicationEventPublisher.publishEvent(new BotRemoveEvent(fullName));
    }
}