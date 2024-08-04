package com.example.githubprconsumer.message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultMessageRepository extends JpaRepository<DefaultMessage, Long> {
}
