package com.example.githubprconsumer.global.auth.application;

import com.example.githubprconsumer.github.application.dto.GithubUser;
import com.example.githubprconsumer.global.auth.application.dto.OAuth2CodeResponse;
import com.example.githubprconsumer.global.auth.application.dto.TokenResponseDto;
import com.example.githubprconsumer.member.application.MemberService;
import com.example.githubprconsumer.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final JwtProvider jwtProvider;

    private final MemberService memberService;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;
    
    public TokenResponseDto login(String code) {
        String accessToken = getGitHubAccessToken(code);
        GithubUser user = getGithubUserInfo(accessToken);
        Member member = memberService.createIfNotExist(user.login());
        String jwtToken = jwtProvider.createToken(member.getLogin());
        return new TokenResponseDto(jwtToken);
    }

    private String getGitHubAccessToken(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("code", code);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<OAuth2CodeResponse> response = restTemplate.postForEntity(tokenUrl, entity, OAuth2CodeResponse.class);

        return Objects.requireNonNull(response.getBody()).accessToken();
    }

    private GithubUser getGithubUserInfo(String accessToken) {
        String userInfoUrl = "https://api.github.com/user";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<GithubUser> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, GithubUser.class);
        return Objects.requireNonNull(response.getBody());
    }
}
