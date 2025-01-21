package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID AUTHENTICATED_USER_ID = UUID.randomUUID();
    private static final User TEST_USER = new User(
            "USA",
            "testuser@example.com",
            "encodedPassword",
            User.Role.USER,
            "Test Salutation",
            "Detailed Salutation",
            "testuser",
            "/pictures/profile.jpg",
            true
    );

    private static final UserDto UPDATED_USER_DTO = new UserDto(
            "USA",
            "Updated Salutation",
            "Updated Detailed Salutation",
            "updateduser@example.com",
            "updateduser",
            "Updated@Password1",
            "/pictures/updated.jpg",
            "USER",
            true
    );

    @BeforeEach
    void setUp() {
        lenient().when(userService.getAuthenticatedUserId()).thenReturn(AUTHENTICATED_USER_ID);
    }

    @Test
    void getAllUsers_returnsUserList() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(List.of(TEST_USER));

        // Act
        List<User> users = userController.getAllUsers();

        // Assert
        assertThat(users).containsExactly(TEST_USER);
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUser_returnsUserForAdmin() {
        // Arrange
        when(userService.isAdmin()).thenReturn(true);
        when(userService.getUser(USER_ID)).thenReturn(TEST_USER);

        // Act
        ResponseEntity<?> response = userController.getUser(USER_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(TEST_USER);
        verify(userService, times(1)).getUser(USER_ID);
    }

    @Test
    void getUser_returnsForbiddenForUnauthorizedAccess() {
        // Arrange
        when(userService.isAdmin()).thenReturn(false);

        // Act
        ResponseEntity<?> response = userController.getUser(USER_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "You are not authorized to view information about this user"));
        verify(userService, never()).getUser(USER_ID);
    }

    @Test
    void getUser_returnsUserForSelfAccess() {
        // Arrange
        when(userService.isAdmin()).thenReturn(false);
        when(userService.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(userService.getUser(USER_ID)).thenReturn(TEST_USER);

        // Act
        ResponseEntity<?> response = userController.getUser(USER_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(TEST_USER);
        verify(userService, times(1)).getUser(USER_ID);
    }

    @Test
    void updateUser_updatesUserForAdmin() {
        // Arrange
        when(userService.isAdmin()).thenReturn(true);
        when(userService.updateUser(USER_ID, UPDATED_USER_DTO)).thenReturn(TEST_USER);

        // Act
        ResponseEntity<Map<String, Object>> response = userController.updateUser(USER_ID, UPDATED_USER_DTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "User successfully updated");
        assertThat(response.getBody()).containsEntry("user", TEST_USER);
        verify(userService, times(1)).updateUser(USER_ID, UPDATED_USER_DTO);
    }

    @Test
    void updateUser_returnsForbiddenForUnauthorizedAccess() {
        // Arrange
        when(userService.isAdmin()).thenReturn(false);
        when(userService.getAuthenticatedUserId()).thenReturn(AUTHENTICATED_USER_ID);

        // Act
        ResponseEntity<Map<String, Object>> response = userController.updateUser(USER_ID, UPDATED_USER_DTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("error", "You are not authorized to edit this user");
        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    void deleteUser_deletesUser() {
        // Arrange
        doNothing().when(userService).deleteUser(USER_ID);

        // Act
        ResponseEntity<?> response = userController.deleteUser(USER_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService, times(1)).deleteUser(USER_ID);
    }
}

