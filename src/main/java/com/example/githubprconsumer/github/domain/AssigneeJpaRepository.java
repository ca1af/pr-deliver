package com.example.githubprconsumer.github.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssigneeJpaRepository extends JpaRepository<Assignee, Long> {

    List<Assignee> findAllByRegisterCollaboratorId(Long registerCollaboratorId);

}
