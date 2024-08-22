package com.example.githubprconsumer.message.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByMessengerId(Long messengerId);

    void deleteAllByMessengerId(Long messengerId);

    List<Message> findAllByMessengerId(Long messengerId);
}
