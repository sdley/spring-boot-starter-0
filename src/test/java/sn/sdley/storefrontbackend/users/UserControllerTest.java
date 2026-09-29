package sn.sdley.storefrontbackend.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sn.sdley.storefrontbackend.auth.JwtAuthenticationFilter;
import sn.sdley.storefrontbackend.auth.JwtService;
import sn.sdley.storefrontbackend.auth.SecurityConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void validatesRegistrationRequestBeforeCallingService() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "Not-An-Email",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    void createsUserAndReturnsItsLocation() throws Exception {
        when(userService.registerUser(any()))
                .thenReturn(new UserDto(42L, "A User", "user@example.com", null));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "A User",
                                  "email": "user@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/users/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(userService).registerUser(any(RegisterUserRequest.class));
    }

    @Test
    void returnsBadRequestForDuplicateEmail() throws Exception {
        when(userService.registerUser(any())).thenThrow(new DuplicateUserException());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "A User",
                                  "email": "user@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email is already registered."));
    }
}
