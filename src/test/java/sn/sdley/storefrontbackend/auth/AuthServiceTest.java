package sn.sdley.storefrontbackend.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sn.sdley.storefrontbackend.users.User;
import sn.sdley.storefrontbackend.users.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final AuthService authService = new AuthService(userRepository, mock(JwtService.class),
            mock(AuthenticationManager.class));

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsAuthenticatedUser() {
        var user = new User();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(42L, null, List.of()));
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));

        assertThat(authService.getCurrentUser()).isSameAs(user);
        verify(userRepository).findById(42L);
    }

    @Test
    void returnsNullWhenAuthenticatedUserNoLongerExists() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(42L, null, List.of()));

        assertThat(authService.getCurrentUser()).isNull();
        verify(userRepository).findById(42L);
    }

    @Test
    void rejectsMissingAuthentication() {
        assertThatThrownBy(authService::getCurrentUser)
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        verifyNoInteractions(userRepository);
    }

    @Test
    void rejectsPrincipalThatIsNotAUserId() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of()));

        assertThatThrownBy(authService::getCurrentUser)
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        verifyNoInteractions(userRepository);
    }

    @Test
    void rejectsNullPrincipal() {
        var authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(authService::getCurrentUser)
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        verifyNoInteractions(userRepository);
    }

    @Test
    void rejectsUnauthenticatedPrincipal() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(42L, null));

        assertThatThrownBy(authService::getCurrentUser)
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        verifyNoInteractions(userRepository);
    }
}
