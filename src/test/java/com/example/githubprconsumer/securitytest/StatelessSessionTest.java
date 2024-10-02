package com.example.githubprconsumer.securitytest;

import com.example.githubprconsumer.global.auth.JwtAuthenticationFilter;
import com.example.githubprconsumer.global.auth.application.OAuth2Service;
import com.example.githubprconsumer.global.auth.presentation.AuthController;
import com.example.githubprconsumer.securitytest.config.StatelessSecurityConfig;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class})
@Import(StatelessSecurityConfig.class)
@DisplayName("세션 상태가 STATELESS 일 때를 테스트한다.")
class StatelessSessionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private OAuth2Service oAuth2Service;

    @Test
    @WithMockOAuth2User(attributes = {"name:99999"})
    @DisplayName("SecurityContext 는 세션에 저장되지 않는다. (STATELESS)")
    void testSecurityContextNotStoredInSession_Stateless() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/oauth2/login"))
                .andExpect(status().isOk())
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNull(); // Stateless 설정에서는 세션이 null이어야 함
    }

    @Test
    void testWebMvcTestUsesMockSessions() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/oauth2/login"))
                .andExpect(status().isOk())
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNull(); // 아무 설정이 없다면 세션을 생성하지 않는다.
    }
}