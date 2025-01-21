package at.technikum.springrestbackend.security.jwt;

import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private static final String EMAIL = "testuser@example.com";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "encodedPassword";
    private static final User TEST_USER = new User(
            "USA",
            EMAIL,
            PASSWORD,
            User.Role.ADMIN,
            "Mr.",
            "Detailed Salutation",
            USERNAME,
            "/pictures/profile.jpg",
            true
    );

    @BeforeEach
    void setUp() {
        lenient().when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(TEST_USER));
        lenient().when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(TEST_USER));
    }

    @Test
    void loadUserByUsername_findsUserByEmail() {
        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(EMAIL);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(EMAIL); // Uses email as the principal username
        assertThat(userDetails.getPassword()).isEqualTo(PASSWORD);
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN"); // Role converted to ROLE_ADMIN

        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_findsUserByUsername() {
        // Arrange
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(USERNAME);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USERNAME); // Uses username as the principal username
        assertThat(userDetails.getPassword()).isEqualTo(PASSWORD);
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");

        verify(userRepository, times(1)).findByEmail(USERNAME);
        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    @Test
    void loadUserByUsername_userNotFoundThrowsException() {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("unknown@example.com")).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email or username: unknown@example.com");

        verify(userRepository, times(1)).findByEmail("unknown@example.com");
        verify(userRepository, times(1)).findByUsername("unknown@example.com");
    }
}

