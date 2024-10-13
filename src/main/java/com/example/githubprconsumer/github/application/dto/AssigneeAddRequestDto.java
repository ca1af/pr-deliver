package com.example.githubprconsumer.github.application.dto;

import com.example.githubprconsumer.github.domain.Assignee;

public record AssigneeAddRequestDto(Long assigneeCollaboratorId, String assigneeLogin) {
    public Assignee toEntity(Long registerCollaboratorId){
        return new Assignee(registerCollaboratorId, assigneeCollaboratorId, assigneeLogin);
    }
}
