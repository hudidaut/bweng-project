package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.entity.Product;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.ProductRepository;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public FileController(FileService fileService, ProductRepository productRepository, UserRepository userRepository) {
        this.fileService = fileService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/products/{id}/image")
    public ResponseEntity<?> getProductImage(@PathVariable UUID id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product product = productOpt.get();
        String imageUrl = product.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Ensure the objectKey excludes the bucket name and leading slash
        String objectKey = imageUrl.startsWith("/pictures/")
                ? imageUrl.substring("/pictures/".length())
                : imageUrl.startsWith("pictures/")
                ? imageUrl.substring("pictures/".length())
                : imageUrl;

        System.out.println("Corrected Object Key for presigned URL: " + objectKey);

        String presignedUrl = fileService.getPresignedUrl(objectKey);
        return ResponseEntity.ok(presignedUrl);
    }

    @PostMapping("/products/{id}/upload-image")
    public ResponseEntity<?> uploadProductImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        try {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            String imageUrl = fileService.uploadFile(file);
            Product product = productOpt.get();
            product.setImageUrl(imageUrl);
            productRepository.save(product);
            return ResponseEntity.ok(Map.of("message", "Image uploaded successfully", "imageUrl", imageUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/users/{id}/profile-picture")
    public ResponseEntity<?> getUserProfilePicture(@PathVariable UUID id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        String profilePictureUrl = user.getProfilePictureUrl();

        if (profilePictureUrl == null || profilePictureUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Ensure the object key is sanitized
        String objectKey = profilePictureUrl.startsWith("/pictures/")
                ? profilePictureUrl.substring("/pictures/".length())
                : profilePictureUrl.startsWith("pictures/")
                ? profilePictureUrl.substring("pictures/".length())
                : profilePictureUrl;

        System.out.println("Corrected Object Key: " + objectKey);

        // Generate the presigned URL using the sanitized object key
        String presignedUrl = fileService.getPresignedUrl(objectKey);
        System.out.println("Generated Presigned URL: " + presignedUrl);

        // Return the presigned URL without further modification
        return ResponseEntity.ok(presignedUrl);
    }


@PostMapping("/users/{id}/upload-profile-picture")
public ResponseEntity<?> uploadProfilePicture(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
    Optional<User> userOpt = userRepository.findById(id);
    if (userOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    String profilePictureUrl = fileService.uploadFile(file);
    User user = userOpt.get();
    user.setProfilePictureUrl(profilePictureUrl);
    userRepository.save(user);
    return ResponseEntity.ok("Profile picture uploaded successfully");
}
}


