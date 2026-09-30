package sn.sdley.storefrontbackend;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import sn.sdley.storefrontbackend.support.MySqlTestConfiguration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(properties = {"app.seed.enabled=false", "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.user=test", "spring.flyway.password=test"})
@AutoConfigureMockMvc
@Import(MySqlTestConfiguration.class)
class SpringBootStoreFrontApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void homePathRendersThymeleafLandingPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("siteName", "Storefront API"))
                .andExpect(content().string(containsString("<title>Storefront API | Build commerce experiences</title>")))
                .andExpect(content().string(not(containsString("th:href"))))
                .andExpect(content().string(containsString("Build your storefront")))
                .andExpect(content().string(containsString("Made with")))
                .andExpect(content().string(containsString("href=\"https://sdley.github.io/\" target=\"_blank\" rel=\"noopener noreferrer\"")))
                .andExpect(content().string(containsString("sdley (Souleymane DIALLO)")));
    }

    @Test
    void indexHtmlRendersTheSameTemplate() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("Storefront API")));
    }

    @Test
    void registeredCustomerCanLogInAndReadTheirProfile() throws Exception {
        var email = "integration-" + java.util.UUID.randomUUID() + "@example.com";
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Integration Customer",
                                  "email": "%s",
                                  "password": "secret123"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated());

        var loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "secret123"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();

        var accessToken = objectMapper.readTree(loginResponse.getResponse().getContentAsString())
                .get("token").asText();
        org.assertj.core.api.Assertions.assertThat(loginResponse.getResponse().getCookie("refreshToken"))
                .isNotNull()
                .extracting(jakarta.servlet.http.Cookie::isHttpOnly)
                .isEqualTo(true);

        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(email)));
    }

    @Test
    void protectedApiRejectsRequestsWithoutAnAccessToken() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

}
