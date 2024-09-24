package com.example.githubprconsumer.messenger.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessengerAliasRepository extends JpaRepository<MessengerAlias, Long> {
    List<MessengerAlias> findAllByMessengerId(Long messengerId);
}
