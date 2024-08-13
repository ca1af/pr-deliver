package com.example.githubprconsumer.member;

public record SignupRequestDto(Long id, String githubEmail) {
    public Member toEntity(){
        return new Member(id, githubEmail);
    }
}
