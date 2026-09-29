package sn.sdley.storefrontbackend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sn.sdley.storefrontbackend.admin.AdminController;
import sn.sdley.storefrontbackend.common.HomeController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AdminController.class, HomeController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void homePageAndIndexArePublic() throws Exception {
        for (var path : new String[]{"/", "/index.html"}) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Build your storefront")));
            mockMvc.perform(head(path)).andExpect(status().isOk());
        }
    }

    @Test
    void documentationPathsPassSecurity() throws Exception {
        // This MVC slice omits springdoc handlers, so a 404 means security allowed the request through.
        for (var path : new String[]{
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/swagger-ui/swagger-ui.css",
                "/v3/api-docs",
                "/v3/api-docs/swagger-config",
                "/v3/api-docs.yaml"
        }) {
            mockMvc.perform(get(path))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    void adminApiStillRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/admin/hello"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicCartRegistrationLoginAndWebhookPathsPassSecurity() throws Exception {
        mockMvc.perform(get("/carts/00000000-0000-0000-0000-000000000001"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/users"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/checkout/webhook"))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminEndpointRequiresTheAdminRole() throws Exception {
        mockMvc.perform(get("/admin/hello").with(user("customer").roles("USER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/hello").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello Admin!"));
    }
}
