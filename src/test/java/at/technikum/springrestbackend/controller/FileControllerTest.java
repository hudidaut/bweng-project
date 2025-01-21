package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.entity.Product;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.ProductRepository;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.service.FileService;
import at.technikum.springrestbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private FileService fileService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FileController fileController;

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID AUTHENTICATED_USER_ID = UUID.randomUUID();
    private static final String IMAGE_URL = "/pictures/product-image.jpg";
    private static final String PROFILE_PICTURE_URL = "/pictures/profile-picture.jpg";
    private static final String PRESIGNED_URL = "http://example.com/presigned-url";

    private static final Product TEST_PRODUCT = new Product(
            "Test Product",
            100.0,
            "Description",
            "Category",
            10,
            IMAGE_URL,
            "creator",
            "updater"
    );

    private static final User TEST_USER = new User(
            "Country",
            "email@example.com",
            "encodedPassword",
            User.Role.USER,
            "Mr.",
            "Salutation",
            "username",
            PROFILE_PICTURE_URL,
            true
    );

    @BeforeEach
    void setUp() {
        lenient().when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(TEST_PRODUCT));
        lenient().when(userRepository.findById(USER_ID)).thenReturn(Optional.of(TEST_USER));
        lenient().when(fileService.getPresignedUrl(anyString())).thenReturn(PRESIGNED_URL);
        lenient().when(userService.getAuthenticatedUserId()).thenReturn(AUTHENTICATED_USER_ID);
        lenient().when(userService.isAdmin()).thenReturn(false);
    }

    // Tests for Product Image
    @Test
    void getProductImage_returnsPresignedUrl() {
        // Act
        ResponseEntity<?> response = fileController.getProductImage(PRODUCT_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(PRESIGNED_URL);
        verify(fileService, times(1)).getPresignedUrl("product-image.jpg");
    }

    @Test
    void getProductImage_productNotFound_returnsNotFound() {
        // Arrange
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = fileController.getProductImage(PRODUCT_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(fileService, never()).getPresignedUrl(anyString());
    }

    @Test
    void uploadProductImage_uploadsAndUpdatesProduct() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileService.uploadFile(mockFile)).thenReturn(IMAGE_URL);

        // Act
        ResponseEntity<?> response = fileController.uploadProductImage(PRODUCT_ID, mockFile);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(Map.of(
                "message", "Image uploaded successfully",
                "imageUrl", IMAGE_URL
        ));
        verify(fileService, times(1)).uploadFile(mockFile);
        verify(productRepository, times(1)).save(TEST_PRODUCT);
    }

    @Test
    void uploadProductImage_productNotFound_returnsNotFound() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = fileController.uploadProductImage(PRODUCT_ID, mockFile);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(fileService, never()).uploadFile(mockFile);
    }

    @Test
    void uploadProductImage_throwsIllegalArgumentException_returnsBadRequest() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileService.uploadFile(mockFile)).thenThrow(new IllegalArgumentException("Invalid file"));

        // Act
        ResponseEntity<?> response = fileController.uploadProductImage(PRODUCT_ID, mockFile);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Invalid file"));
        verify(fileService, times(1)).uploadFile(mockFile);
    }


    @Test
    void getUserProfilePicture_unauthorizedAccess_returnsForbidden() {
        // Arrange
        when(userService.isAdmin()).thenReturn(false);
        when(userService.getAuthenticatedUserId()).thenReturn(UUID.randomUUID()); // Simulate a different user

        // Act
        ResponseEntity<?> response = fileController.getUserProfilePicture(USER_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "You are not authorized to view information about this user"));
        verify(fileService, never()).getPresignedUrl(anyString());
    }

    @Test
    void uploadProfilePicture_userNotFound_returnsNotFound() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = fileController.uploadProfilePicture(USER_ID, mockFile);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(fileService, never()).uploadFile(mockFile);
    }

    @Test
    void uploadProfilePicture_unauthorizedAccess_returnsForbidden() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(userService.isAdmin()).thenReturn(false);
        when(userService.getAuthenticatedUserId()).thenReturn(UUID.randomUUID()); // Simulate a different user

        // Act
        ResponseEntity<?> response = fileController.uploadProfilePicture(USER_ID, mockFile);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "You are not authorized to view information about this user"));
        verify(fileService, never()).uploadFile(mockFile);
    }
}
