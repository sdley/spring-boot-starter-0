package sn.sdley.storefrontbackend.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sn.sdley.storefrontbackend.users.UserDto;
import sn.sdley.storefrontbackend.users.UserMapper;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for signing in and managing access tokens")
public class AuthController {
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Sign in",
            description = "Authenticates with an email and password. Returns an access token and sets an HttpOnly refresh-token cookie."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication succeeded"),
            @ApiResponse(responseCode = "400", description = "Request validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Email or password is incorrect", content = @Content)
    })
    public JwtResponse login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User email address and password.", required = true)
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        var loginResult = authService.login(request);

        var refreshToken = loginResult.getRefreshToken().toString();
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge((int) jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true); // Set to true if using HTTPS
        response.addCookie(cookie);

        return new JwtResponse(loginResult.getAccessToken().toString());
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh an access token",
            description = "Issues a new access token using the refresh-token cookie set during sign-in."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token refreshed"),
            @ApiResponse(responseCode = "400", description = "Refresh-token cookie is missing", content = @Content),
            @ApiResponse(responseCode = "500", description = "Refresh token is invalid or expired", content = @Content)
    })
    public JwtResponse refresh(@CookieValue(value = "refreshToken") String refreshToken) {
        var accessToken = authService.refreshAccessToken(refreshToken);
        return new JwtResponse(accessToken.toString());
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current user", description = "Returns the profile associated with the supplied access token.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content),
            @ApiResponse(responseCode = "404", description = "Current user does not exist", content = @Content)
    })
    public ResponseEntity<UserDto> getCurrentUser() {
        var user = authService.getCurrentUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
