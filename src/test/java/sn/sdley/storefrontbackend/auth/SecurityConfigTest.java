package sn.sdley.storefrontbackend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sn.sdley.storefrontbackend.admin.AdminController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void homePageAndStaticIndexArePublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Build your storefront")));
        mockMvc.perform(head("/")).andExpect(status().isOk());
        mockMvc.perform(head("/index.html")).andExpect(status().isOk());
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
    }
}
