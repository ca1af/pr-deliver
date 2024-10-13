package com.example.githubprconsumer.github.application;

import com.example.githubprconsumer.github.application.dto.AssigneeAddRequestDto;
import com.example.githubprconsumer.github.domain.Assignee;
import com.example.githubprconsumer.github.domain.AssigneeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssigneeService {

    private final AssigneeJpaRepository assigneeJpaRepository;

    public void addAssignee(Long registerCollaboratorId, List<AssigneeAddRequestDto> assigneeCollaboratorIds){
        assigneeCollaboratorIds.stream()
                .map(each -> each.toEntity(registerCollaboratorId))
                .forEach(assigneeJpaRepository::save);
    }

    public List<Assignee> getAssignees(Long registerCollaboratorId){
        return assigneeJpaRepository.findAllByRegisterCollaboratorId(registerCollaboratorId);
    }
}
