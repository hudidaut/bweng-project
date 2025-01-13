package at.technikum.springrestbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserDto(
        UUID id,
        @NotBlank(message = "Country is required") String country,
        @NotBlank(message = "Salutation is required") String salutation,
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Username is required")
        @Size(min = 5, message = "Username must be at least 5 characters long") String username,
        String profilePictureUrl,
        String role
) {
}
