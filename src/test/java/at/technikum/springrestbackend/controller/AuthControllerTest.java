package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.exception.ResourceNotFoundException;
import at.technikum.springrestbackend.service.UserService;
import at.technikum.springrestbackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "testuser@example.com";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "$2a$10$D9qZ.1s6xUuSDlzB/LBMEOLiYd7YwPAW5PTLNBH.oZWnW.YXO/lfy"; // BCrypt hash for "rawPassword"
    private static final String RAW_PASSWORD = "rawPassword";
    private static final String TOKEN = "jwt-token";

    private static final User TEST_USER = new User(
            "USA",
            EMAIL,
            PASSWORD,
            User.Role.USER,
            "Test Salutation",
            "Detailed Salutation",
            USERNAME,
            "/pictures/profile.jpg",
            true
    );

    @BeforeEach
    void setUp() {
        // Mock user lookup
        lenient().when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(TEST_USER));
        lenient().when(userService.findByUsername(USERNAME)).thenReturn(Optional.of(TEST_USER));

        // Mock JWT token generation
        lenient().when(jwtUtil.generateToken(EMAIL, "USER")).thenReturn(TOKEN);

        // Mock password comparison
        lenient().when(passwordEncoder.matches(eq(RAW_PASSWORD), eq(PASSWORD))).thenReturn(true);
    }

    @Test
    void register_createsNewUser() {
        // Arrange
        doNothing().when(userService).registerUser(any(User.class));

        // Act
        ResponseEntity<?> response = authController.register(TEST_USER);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // Use getStatusCode() here
        assertThat(response.getBody()).isEqualTo(Map.of("message", "User registered successfully"));
        verify(userService, times(1)).registerUser(TEST_USER);
    }

    @Test
    void login_withInvalidCredentials_throwsResourceNotFoundException() {
        // Arrange
        when(userService.findByEmail("wrong@example.com")).thenReturn(Optional.empty());
        when(userService.findByUsername("wrong@example.com")).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> authController.login(Map.of("usernameOrEmail", "wrong@example.com", "password", "wrongPassword")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invalid credentials");

        verify(userService, times(1)).findByEmail("wrong@example.com");
        verify(userService, times(1)).findByUsername("wrong@example.com");
    }
}
