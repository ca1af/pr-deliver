package com.example.githubprconsumer.github.application;

import com.example.githubprconsumer.github.application.dto.GithubCollaboratorInfo;
import com.example.githubprconsumer.github.domain.Assignee;
import com.example.githubprconsumer.github.domain.Collaborator;
import com.example.githubprconsumer.github.domain.CollaboratorException;
import com.example.githubprconsumer.github.domain.CollaboratorJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Log4j2
public class CollaboratorService {

    private final GithubApiService githubApiService;
    private final CollaboratorJpaRepository collaboratorJpaRepository;
    private final AssigneeService assigneeService;
    private static final String BOT_LOGIN = "calafs-review-bot";

    public void addCollaborators(Long repositoryId, String fullName){
        List<GithubCollaboratorInfo> collaborators = new ArrayList<>(githubApiService.getCollaborators(fullName));
        removeBotAccount(fullName, collaborators);

        // 성능 상 문제가 크지 않으므로 아래와 같이 픽스. 추후 필요시 JDBC Template(Client) 로 변경
        List<Collaborator> collaboratorEntities = collaborators.stream()
                .map(each -> each.toEntity(repositoryId))
                .toList();

        collaboratorJpaRepository.saveAll(collaboratorEntities);
    }

    private void removeBotAccount(String fullName, List<GithubCollaboratorInfo> collaborators) {
        GithubCollaboratorInfo botAccount = collaborators.stream()
                .filter(each -> each.login().equals(BOT_LOGIN))
                .findFirst()
                .orElseThrow(() -> new CollaboratorException.BotHasNotInvitedException(fullName));

        collaborators.remove(botAccount);
    }

    public Integer getCollaboratorCount(Long repositoryId){
        return collaboratorJpaRepository.countByRepositoryId(repositoryId);
    }

    // TODO : 이 메서드를 없애고, 테스트를 여기로 돌린다.
    public List<Collaborator> getCollaborators(Long repositoryId, String prAuthorLogin){
        List<Collaborator> collaborators = collaboratorJpaRepository.findAllByRepositoryId(repositoryId);

        if (collaborators.isEmpty()) {
            throw new CollaboratorException.CollaboratorNotFoundException(repositoryId);
        }

        Collaborator prAuthor = collaborators.stream()
                .filter(each -> StringUtils.equals(prAuthorLogin, each.getLogin()))
                .findFirst()
                .orElseThrow(() -> new CollaboratorException.CollaboratorNotFoundException(prAuthorLogin));

        List<Collaborator> modifiableCollaborators = new ArrayList<>(collaborators);
        modifiableCollaborators.remove(prAuthor);

        return modifiableCollaborators;
    }

    public List<String> getAssigneeLogins(Long repositoryId, Integer assigneeCount, String authorLogin){
        List<Collaborator> collaborators = collaboratorJpaRepository.findAllByRepositoryId(repositoryId);

        if (collaborators.isEmpty()) {
            throw new CollaboratorException.CollaboratorNotFoundException(repositoryId);
        }

        Collaborator prAuthor = collaborators.stream()
                .filter(each -> StringUtils.equals(authorLogin, each.getLogin()))
                .findFirst()
                .orElseThrow(() -> new CollaboratorException.CollaboratorNotFoundException(authorLogin));

        List<Collaborator> modifiableCollaborators = new ArrayList<>(collaborators);
        modifiableCollaborators.remove(prAuthor);

        List<Assignee> assignees = assigneeService.getAssignees(prAuthor.getId());

        if (assignees.isEmpty()) {
            return modifiableCollaborators
                    .stream()
                    .limit(assigneeCount)
                    .map(Collaborator::getLogin)
                    .toList();
        }

        return assignees.stream()
                .map(Assignee::getAssigneeLogin)
                .toList();
    }
}
