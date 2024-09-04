package com.example.githubprconsumer.securitytest;

import com.example.githubprconsumer.member.Member;
import com.example.githubprconsumer.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityContextTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @WithMockOAuth2User
    @DisplayName("OAuth2 유저 인증 또한 Security Context 안에 Authentication 객체로 저장된다. 이 인스턴스는 주로 사용되는 UserNameAuthenticationToken 이 아닌 OAuth2AuthenticationToken 으로 저장된다.")
    void authenticationObjectStoredInSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();

        assertThat(authentication).isInstanceOf(OAuth2AuthenticationToken.class);
    }

    @Test
    @WithMockOAuth2User(attributes = {"name:myTestOAuth2User"})
    @DisplayName("커스텀 어노테이션의 어트리뷰트를 변경하여 인증 객체의 정보를 컨트롤 할 수 있다.")
    void canControlAuthenticationAttributesWithCustomAnnotation() {
        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = authentication.getPrincipal();

        assertThat(oAuth2User).isNotNull();
        assertThat(oAuth2User.getName()).isEqualTo("myTestOAuth2User");
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("커스텀 어노테이션을 이용해 인증이 필요한 요청을 테스트 할 수 있다.")
    void canTestAuthenticatedRequestsWithCustomAnnotation() throws Exception {
        memberRepository.save(new Member("123"));
        mockMvc.perform(get("/members/me")).andExpect(status().isOk());
    }
}