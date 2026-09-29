package sn.sdley.storefrontbackend.auth;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sn.sdley.storefrontbackend.users.Role;
import sn.sdley.storefrontbackend.users.User;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {
    private static final String SECRET = "01234567890123456789012345678901";

    private JwtConfig jwtConfig;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        jwtConfig.setSecret(SECRET);
        jwtConfig.setAccessTokenExpiration(60);
        jwtConfig.setRefreshTokenExpiration(600);
        jwtService = new JwtService(jwtConfig);
    }

    @Test
    void accessTokenContainsExpectedClaimsAndConfiguredExpiration() {
        var user = new User(42L, "A User", "user@example.com", "password", Role.ADMIN, null, null);
        var token = jwtService.generateAccessToken(user);
        var claims = Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token.toString())
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("email", String.class)).isEqualTo("user@example.com");
        assertThat(claims.get("name", String.class)).isEqualTo("A User");
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(new Date());
        assertThat(claims.getExpiration().getTime() - claims.getIssuedAt().getTime())
                .isBetween(59_000L, 61_000L);

        var parsed = jwtService.parseToken(token.toString());
        assertThat(parsed.getUserId()).isEqualTo(42L);
        assertThat(parsed.getRole()).isEqualTo(Role.ADMIN);
        assertThat(parsed.isExpired()).isFalse();
    }

    @Test
    void refreshTokenUsesRefreshExpiration() {
        var user = new User();
        user.setId(8L);
        user.setEmail("user@example.com");
        user.setName("A User");
        user.setRole(Role.USER);
        var claims = Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(jwtService.generateRefreshToken(user).toString())
                .getPayload();

        assertThat(claims.getExpiration().getTime() - claims.getIssuedAt().getTime())
                .isBetween(599_000L, 601_000L);
    }

    @Test
    void parseRejectsTokenSignedWithAnotherSecret() {
        var user = new User();
        user.setId(8L);
        user.setEmail("user@example.com");
        user.setName("A User");
        user.setRole(Role.USER);
        var token = jwtService.generateAccessToken(user).toString();
        var otherConfig = new JwtConfig();
        otherConfig.setSecret("another-secret-key-that-is-32-bytes-long");

        assertThatThrownBy(() -> new JwtService(otherConfig).parseToken(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid JWT token");
    }

    @Test
    void parseRejectsExpiredToken() {
        var expiration = new Date(System.currentTimeMillis() - 1_000);
        var expiredToken = Jwts.builder()
                .subject("8")
                .issuedAt(new Date(expiration.getTime() - 10_000))
                .expiration(expiration)
                .signWith(jwtConfig.getSecretKey())
                .compact();

        assertThatThrownBy(() -> jwtService.parseToken(expiredToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid JWT token");
    }
}
