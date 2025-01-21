package at.technikum.springrestbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserDto(
        @NotBlank(message = "Country is required") String country,
        @NotBlank(message = "Salutation is required") String salutation,
        String detailedSalutation,
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required") String email,

        @NotBlank(message = "Username is required")
        @Size(min = 5, message = "Username must be at least 5 characters long") String username,

        @NotBlank(message = "Password is required")
        @Size(min = 12, message = "Password must be at least 12 characters long")
        @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
        @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
        @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number")
        @Pattern(regexp = ".*[@$!%*?&].*", message = "Password must contain at least one special character")
        String password,

        String profilePictureUrl,
        String role,
        Boolean isActive
) {
}
