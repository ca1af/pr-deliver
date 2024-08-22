package com.example.githubprconsumer.member;

public record SignupRequestDto(String login) {
    public Member toEntity(){
        return new Member(login);
    }
}
