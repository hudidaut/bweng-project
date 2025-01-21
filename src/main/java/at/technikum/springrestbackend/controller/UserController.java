package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Get all users (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> getUser(@PathVariable UUID id) {
        // Get the currently authenticated user's ID
        UUID authenticatedUserId = userService.getAuthenticatedUserId();

        // If not admin, ensure the user can only retrieve their own data
        if (!userService.isAdmin() && !authenticatedUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to view information about this user"));
        }

        // Retrieve and return the user data
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable UUID id, @RequestBody UserDto userDetails) {
        // Get the currently authenticated user's ID
        UUID authenticatedUserId = userService.getAuthenticatedUserId();

        // If not admin, ensure the user can only edit their own data
        if (!userService.isAdmin() && !authenticatedUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to edit this user"));
        }

        // Proceed with the update
        User updatedUser = userService.updateUser(id, userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User successfully updated");
        response.put("user", updatedUser);

        return ResponseEntity.ok(response);
    }


    // Delete user by ID (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}

