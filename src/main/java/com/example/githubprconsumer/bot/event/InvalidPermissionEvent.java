package com.example.githubprconsumer.bot.event;

public record InvalidPermissionEvent(
        Integer id,
        String login
) {
}
