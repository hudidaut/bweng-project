package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.exception.ResourceNotFoundException;
import at.technikum.springrestbackend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user) {
        this.checkUserProfileExists(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a single user by ID
    public User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Update user details
    public User updateUser(UUID id, UserDto userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userDetails.email() != null) {
            user.setEmail(userDetails.email());
        }
        if (userDetails.username() != null) {
            user.setUsername(userDetails.username());
        }
        if (userDetails.country() != null) {
            user.setCountry(userDetails.country());
        }
        if (userDetails.role() != null) {
            user.setRole(User.Role.valueOf(userDetails.role()));
        }
        if (userDetails.profilePictureUrl() != null) {
            user.setProfilePictureUrl(userDetails.profilePictureUrl());
        }
        if (userDetails.salutation() != null) {
            user.setSalutation(userDetails.salutation());
        }
        if (userDetails.detailedSalutation() != null) {
            user.setDetailedSalutation(userDetails.detailedSalutation());
        }
        if (userDetails.isActive() != null ) {
            user.setIsActive(userDetails.isActive());
        }
        return userRepository.save(user);
    }

    // Delete user
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public UUID getAuthenticatedUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"))
                .getId();
    }

    public boolean isAdmin() {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
        return role.equals("ROLE_ADMIN");
    }

    // Set Placeholder Profile Picture when User is created
    public void checkUserProfileExists(User user){
        if (user.getProfilePictureUrl().isBlank())
        {
            user.setProfilePictureUrl("/pictures/userPlaceHolderPic.png");
        }
    }

}

