package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.ProductDto;
import at.technikum.springrestbackend.entity.Product;
import at.technikum.springrestbackend.exception.ResourceNotFoundException;
import at.technikum.springrestbackend.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private final Product LAPTOP = new Product(
            "Laptop",
            1000.0,
            "High-performance laptop",
            "Electronics",
            10,
            "http://example.com/laptop.jpg",
            "admin",
            "admin"
    );

    @BeforeEach
    void setUp() {
        productRepository.save(LAPTOP);
    }

    @Test
    void getAllProducts_returnsAllProducts() {
        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
        assertThat(products).hasSize(1);
        Product actualProduct = products.get(0);
        assertProductsHaveEqualContent(actualProduct, LAPTOP);
    }

    @Test
    void getProductById_returnsCorrectProduct() {
        // Act
        Product actualProduct = productService.getProduct(LAPTOP.getId());

        // Assert
        assertProductsHaveEqualContent(actualProduct, LAPTOP);
    }

    @Test
    void getProductById_nonExistentProduct_throwsException() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProduct(UUID.randomUUID()));
    }

    @Test
    void addProduct_savesProduct() {
        // Arrange
        ProductDto newProductDto = new ProductDto(
                "Phone",
                700.0,
                "Smartphone with high performance",
                "Electronics",
                20,
                "http://example.com/phone.jpg",
                "user",
                "user"
        );

        // Act
        UUID newProductId = productService.addProduct(newProductDto);
        Product savedProduct = productService.getProduct(newProductId);

        // Assert
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Phone");
        assertThat(savedProduct.getPrice()).isEqualTo(700.0);
    }

    @Test
    void removeProduct_deletesProduct() {
        // Act
        productService.removeProduct(LAPTOP.getId());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProduct(LAPTOP.getId()));
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    private void assertProductsHaveEqualContent(Product actualProduct, Product expectedProduct) {
        assertThat(actualProduct.getName()).isEqualTo(expectedProduct.getName());
        assertThat(actualProduct.getPrice()).isEqualTo(expectedProduct.getPrice());
        assertThat(actualProduct.getDescription()).isEqualTo(expectedProduct.getDescription());
        assertThat(actualProduct.getCategory()).isEqualTo(expectedProduct.getCategory());
        assertThat(actualProduct.getStockQuantity()).isEqualTo(expectedProduct.getStockQuantity());
        assertThat(actualProduct.getImageUrl()).isEqualTo(expectedProduct.getImageUrl());
    }
}

