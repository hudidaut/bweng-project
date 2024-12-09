package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // GET a single user by ID (Admin only)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    // Update user by ID (Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable UUID id, @RequestBody User userDetails) {
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

