package at.technikum.springrestbackend.util;

import at.technikum.springrestbackend.property.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.SecretKey;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class JwtUtilTest {

    @Mock
    private JwtProperties jwtProperties;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jwtProperties.getSecret()).thenReturn("verysecretkey12345678901234567890"); // Mock the secret
        jwtUtil = new JwtUtil(jwtProperties);
        jwtUtil.init(); // Initialize the secretKey in JwtUtil
    }

    @Test
    void generateToken_createsValidToken() {
        // Act
        String token = jwtUtil.generateToken("testuser", "USER");

        // Assert
        assertThat(token).isNotBlank();
        Claims claims = jwtUtil.getClaims(token);
        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("role")).isEqualTo("ROLE_USER");
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        // Arrange
        String token = jwtUtil.generateToken("testuser", "USER");

        // Act
        String username = jwtUtil.extractUsername(token);

        // Assert
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void extractRole_returnsCorrectRole() {
        // Arrange
        String token = jwtUtil.generateToken("testuser", "ADMIN");

        // Act
        String role = jwtUtil.extractRole(token);

        // Assert
        assertThat(role).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void validateToken_returnsTrueForValidToken() {
        // Arrange
        String token = jwtUtil.generateToken("testuser", "USER");

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_returnsFalseForExpiredToken() {
        // Arrange
        // Generate an expired token by manipulating the expiration time
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject("testuser")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Set to the past
                .signWith(secretKey, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        // Act
        boolean isValid = jwtUtil.validateToken(expiredToken);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_returnsFalseForInvalidToken() {
        // Arrange
        String invalidToken = "this.is.not.a.valid.token";

        // Act
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void getClaims_throwsExceptionForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token";

        // Assert
        assertThatThrownBy(() -> jwtUtil.getClaims(invalidToken))
                .isInstanceOf(io.jsonwebtoken.MalformedJwtException.class)
                .hasMessageContaining("JWT strings must contain exactly 2 period characters");
    }
}

