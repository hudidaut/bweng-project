package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.exception.ResourceNotFoundException;
import at.technikum.springrestbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final User TEST_USER = new User(
            "USA",
            "testuser@example.com",
            "testPassword",
            User.Role.USER,
            "User",
            "Detailed Salutation",
            "testuser",
            "/pictures/profile.jpg",
            true
    );

    private static final UserDto TEST_USER_DTO = new UserDto(
            "USA",
            "User",
            "Detailed Salutation",
            "testuser@example.com",
            "testuser",
            "Test@Password1",
            "/pictures/profile.jpg",
            "USER",
            true
    );

    @BeforeEach
    void setUp() {
        lenient().when(userRepository.findById(USER_ID)).thenReturn(Optional.of(TEST_USER));
        lenient().when(userRepository.findAll()).thenReturn(List.of(TEST_USER));
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void getAllUsers_returnsUserList() {
        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertThat(users).containsExactly(TEST_USER);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUser_returnsUser() {
        // Act
        User user = userService.getUser(USER_ID);

        // Assert
        assertThat(user).isEqualTo(TEST_USER);
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void getUser_throwsResourceNotFoundException() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> userService.getUser(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void registerUser_encodesPasswordAndSavesUser() {
        // Arrange
        User newUser = new User(
                "Canada",
                "newuser@example.com",
                "newPassword",
                User.Role.USER,
                "New User",
                "New Detailed Salutation",
                "newuser",
                "/pictures/new.jpg",
                true
        );

        // Act
        userService.registerUser(newUser);

        // Assert
        assertThat(newUser.getPassword()).isEqualTo("encodedPassword");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void updateUser_updatesUserFields() {
        // Arrange
        UserDto updatedDto = new UserDto(
                "Canada",
                "Updated User",
                "Updated Salutation",
                "updateduser@example.com",
                "updateduser",
                "Updated@Password1",
                "/pictures/updated.jpg",
                "ADMIN",
                false
        );

        User updatedUser = new User(
                "Canada",
                "updateduser@example.com",
                "Updated@Password1",
                User.Role.ADMIN,
                "Updated User",
                "Updated Salutation",
                "updateduser",
                "/pictures/updated.jpg",
                false
        );
        updatedUser.setId(USER_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(TEST_USER));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser); // Mock save behavior

        // Act
        User result = userService.updateUser(USER_ID, updatedDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("updateduser@example.com");
        assertThat(result.getUsername()).isEqualTo("updateduser");
        assertThat(result.getCountry()).isEqualTo("Canada");
        assertThat(result.getRole()).isEqualTo(User.Role.ADMIN);
        assertThat(result.getProfilePictureUrl()).isEqualTo("/pictures/updated.jpg");
        assertThat(result.getIsActive()).isFalse();
        verify(userRepository, times(1)).save(TEST_USER);
    }

    @Test
    void deleteUser_removesUser() {
        // Act
        userService.deleteUser(USER_ID);

        // Assert
        verify(userRepository, times(1)).delete(TEST_USER);
    }

    @Test
    void deleteUser_throwsResourceNotFoundException() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> userService.deleteUser(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: " + unknownId);
    }

    @Test
    void findByEmail_returnsUser() {
        // Arrange
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(TEST_USER));

        // Act
        Optional<User> result = userService.findByEmail("testuser@example.com");

        // Assert
        assertThat(result).isPresent().contains(TEST_USER);
    }

    @Test
    void findByUsername_returnsUser() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(TEST_USER));

        // Act
        Optional<User> result = userService.findByUsername("testuser");

        // Assert
        assertThat(result).isPresent().contains(TEST_USER);
    }

    @Test
    void checkUserProfileExists_setsPlaceholderWhenUrlIsBlank() {
        // Arrange
        User user = new User();
        user.setProfilePictureUrl(""); // Blank URL

        // Act
        userService.checkUserProfileExists(user);

        // Assert
        assertThat(user.getProfilePictureUrl()).isEqualTo("/pictures/userPlaceHolderPic.png");
    }

    @Test
    void checkUserProfileExists_doesNotChangeNonBlankUrl() {
        // Arrange
        User user = new User();
        user.setProfilePictureUrl("/pictures/existing.jpg"); // Non-blank URL

        // Act
        userService.checkUserProfileExists(user);

        // Assert
        assertThat(user.getProfilePictureUrl()).isEqualTo("/pictures/existing.jpg");
    }

}
