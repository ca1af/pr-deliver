package com.example.githubprconsumer.securitytest;

import com.example.githubprconsumer.auth.JwtAuthenticationFilter;
import com.example.githubprconsumer.auth.presentation.AuthController;
import com.example.githubprconsumer.securitytest.config.IfRequiredSecurityConfig;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class})
@Import(IfRequiredSecurityConfig.class)
@DisplayName("세션 상태가 IF_REQUIRED 일 때를 테스트한다.")
class WithSessionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockOAuth2User(attributes = {"name:99999"})
    @DisplayName("SecurityContext 는 세션에 저장된다. (IF_REQUIRED)")
    void testSecurityContextStoredInSession_IfRequired() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/oauth2/login"))
                .andExpect(status().isOk())
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        assertThat(securityContext).isNotNull();

        OAuth2User principal = (OAuth2User) securityContext.getAuthentication().getPrincipal();
        assertThat(principal.getName()).isEqualTo("99999");
    }
}