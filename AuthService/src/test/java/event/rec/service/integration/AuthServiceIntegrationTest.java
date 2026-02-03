package event.rec.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import event.rec.service.TestContainersConfig;
import event.rec.service.TestSecurityConfig;
import event.rec.service.requests.JwtRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@ImportTestcontainers(TestContainersConfig.class)
public class AuthServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFullSignInFlow() throws Exception {
        String registrationRequest =
            """
            {
                "login": "user@example.com",
                "password": "securePassword123",
                "user_type": "USER",
                "full_name": "Петр Петров",
                "phone_number": "+79001234567"
            }
            """;

        mockMvc.perform(post("/auth/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationRequest))
                .andExpect(status().isOk());

        JwtRequest signInRequest = new JwtRequest(
                "user@example.com",
                "securePassword123");

        mockMvc.perform(post("/auth/sign/in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isString());
    }

    @Test
    void testSignInWithWrongCredentials() throws Exception {
        JwtRequest request = new JwtRequest(
                "wrong@example.com",
                "wrongpassword");

        mockMvc.perform(post("/auth/sign/in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
