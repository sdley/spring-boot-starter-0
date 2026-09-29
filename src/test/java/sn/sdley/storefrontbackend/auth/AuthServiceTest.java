package sn.sdley.storefrontbackend.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sn.sdley.storefrontbackend.users.User;
import sn.sdley.storefrontbackend.users.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final AuthService authService = new AuthService(userRepository, jwtService, authenticationManager);

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

    @Test
    void loginAuthenticatesAndReturnsAccessAndRefreshTokens() {
        var request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password");
        var user = new User();
        var accessToken = mock(Jwt.class);
        var refreshToken = mock(Jwt.class);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);

        var response = authService.login(request);

        assertThat(response.getAccessToken()).isSameAs(accessToken);
        assertThat(response.getRefreshToken()).isSameAs(refreshToken);
        verify(authenticationManager).authenticate(argThat(authentication ->
                authentication instanceof UsernamePasswordAuthenticationToken token
                        && token.getPrincipal().equals("user@example.com")
                        && token.getCredentials().equals("password")));
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void loginDoesNotLookUpUserOrGenerateTokensWhenAuthenticationFails() {
        var request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong-password");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(userRepository, jwtService);
    }

    @Test
    void refreshesAccessTokenForValidRefreshToken() {
        var user = new User();
        var parsedToken = mock(Jwt.class);
        var accessToken = mock(Jwt.class);
        when(jwtService.parseToken("refresh-token")).thenReturn(parsedToken);
        when(parsedToken.isExpired()).thenReturn(false);
        when(parsedToken.getUserId()).thenReturn(42L);
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);

        assertThat(authService.refreshAccessToken("refresh-token")).isSameAs(accessToken);

        verify(userRepository).findById(42L);
        verify(jwtService).generateAccessToken(user);
    }

    @Test
    void rejectsMissingOrExpiredRefreshTokenWithoutLookingUpUser() {
        when(jwtService.parseToken("missing-token")).thenReturn(null);
        var expiredToken = mock(Jwt.class);
        when(jwtService.parseToken("expired-token")).thenReturn(expiredToken);
        when(expiredToken.isExpired()).thenReturn(true);

        assertThatThrownBy(() -> authService.refreshAccessToken("missing-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid refresh token");
        assertThatThrownBy(() -> authService.refreshAccessToken("expired-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid refresh token");

        verify(userRepository, never()).findById(any());
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void refreshFailsWhenTokenUserNoLongerExists() {
        var parsedToken = mock(Jwt.class);
        when(jwtService.parseToken("refresh-token")).thenReturn(parsedToken);
        when(parsedToken.isExpired()).thenReturn(false);
        when(parsedToken.getUserId()).thenReturn(42L);
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshAccessToken("refresh-token"))
                .isInstanceOf(java.util.NoSuchElementException.class);

        verify(jwtService, never()).generateAccessToken(any());
    }
}
