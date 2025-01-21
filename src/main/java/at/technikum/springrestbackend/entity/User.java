package at.technikum.springrestbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Salutation is required")
    private String salutation;

    private String detailedSalutation;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 5, message = "Username must be at least 5 characters long")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        USER, ADMIN
    }

    private String profilePictureUrl;

    private Boolean isActive;

    // Default constructor
    public User() {}

    // Constructor
    public User(String country, String email, String password, Role role, String salutation, String detailedSalutation, String username, String profilePictureUrl, boolean isActive) {
        this.country = country;
        this.email = email;
        this.password = password;
        this.role = role;
        this.salutation = salutation;
        this.detailedSalutation = detailedSalutation;
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.isActive = isActive;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getDetailedSalutation() { return detailedSalutation; }

    public void setDetailedSalutation(String detailedSalutation) {  this.detailedSalutation = detailedSalutation;}

    public String getProfilePictureUrl() { return profilePictureUrl; }

    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public boolean getIsActive() { return isActive; }

    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}
