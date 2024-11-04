package at.technikum.springrestbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductDto (@NotBlank String name, Double price) {
}
