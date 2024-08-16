package com.example.githubprconsumer.github;

import com.example.githubprconsumer.bot.api.GithubApiService;
import com.example.githubprconsumer.bot.api.GithubCollaboratorInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Log4j2
public class CollaboratorService {

    private final GithubApiService githubApiService;
    private final CollaboratorJpaRepository collaboratorJpaRepository;

    public void addCollaborators(String fullName){
        List<GithubCollaboratorInfo> collaborators = githubApiService.getCollaborators(fullName);

        // 성능 상 문제가 크지 않으므로 아래와 같이 픽스. 추후 필요시 JDBC Template(Client) 로 변경
        List<Collaborator> collaboratorEntities = collaborators.stream()
                .map(GithubCollaboratorInfo::toEntity)
                .toList();

        collaboratorJpaRepository.saveAll(collaboratorEntities);
    }
}
