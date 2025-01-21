package at.technikum.springrestbackend.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleResourceNotFound_returnsNotFoundResponse() {
        // Arrange
        String errorMessage = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleResourceNotFound(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("error", errorMessage);
    }

    @Test
    void handleValidationExceptions_returnsBadRequestResponse() {
        // Arrange
        String fieldName = "name";
        String errorMessage = "must not be blank";

        BindException bindException = new BindException(new Object(), "target");
        bindException.addError(new FieldError("target", fieldName, errorMessage));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindException.getBindingResult());

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry(fieldName, errorMessage);
    }

    @Test
    void handleAccessDenied_returnsForbiddenResponse() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleAccessDenied(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("error", "Access is denied");
    }

    @Test
    void handleGenericException_returnsInternalServerErrorResponse() {
        // Arrange
        String errorDetails = "Unexpected error";
        Exception exception = new Exception(errorDetails);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleGenericException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody())
                .containsEntry("error", "An unexpected error occurred")
                .containsEntry("details", errorDetails);
    }
}

