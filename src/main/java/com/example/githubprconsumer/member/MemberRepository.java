package com.example.githubprconsumer.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLogin(String githubEmail);
    boolean existsByLogin(String login);
}
