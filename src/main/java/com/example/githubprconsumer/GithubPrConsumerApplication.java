package com.example.githubprconsumer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "https://pr-deliver.shop", description = "PR-Deliver")})
public class GithubPrConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubPrConsumerApplication.class, args);
    }

}
