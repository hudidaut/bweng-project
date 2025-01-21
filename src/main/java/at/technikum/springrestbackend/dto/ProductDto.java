package at.technikum.springrestbackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductDto(
        @NotNull(message = "Product name cannot be null") String name,
        @NotNull(message = "Product price cannot be null")
        @Min(value = 0, message = "Product price must be a positive value") Double price,
        @Size(max = 500, message = "Description must be 500 characters or fewer") String description,
        @NotNull(message = "Category cannot be null") String category,
        @Min(value = 0, message = "Stock quantity must be zero or a positive value") Integer stockQuantity,
        String imageUrl,
        String createdByUserName,
        String updatedByUserName
) {
}