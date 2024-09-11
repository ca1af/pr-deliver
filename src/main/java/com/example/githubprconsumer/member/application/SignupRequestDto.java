package com.example.githubprconsumer.member.application;

import com.example.githubprconsumer.member.domain.Member;

public record SignupRequestDto(String login) {
    public Member toEntity(){
        return new Member(login);
    }
}
