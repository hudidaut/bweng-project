package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.ProductDto;
import at.technikum.springrestbackend.entity.Product;
import at.technikum.springrestbackend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private ProductController productController;

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final Product TEST_PRODUCT = new Product(
            "Test Product",
            99.99,
            "Test Description",
            "Test Category",
            10,
            "http://example.com/image.jpg",
            "testuser",
            "testuser"
    );

    private static final ProductDto TEST_PRODUCT_DTO = new ProductDto(
            "Updated Product",
            79.99,
            "Updated Description",
            "Updated Category",
            15,
            "http://example.com/updated.jpg",
            null,
            "updateduser"
    );

    @BeforeEach
    void setUp() {
        lenient().when(productService.getProduct(PRODUCT_ID)).thenReturn(TEST_PRODUCT);
    }

    @Test
    void getProducts_returnsProductList() {
        // Arrange
        when(productService.getAllProducts()).thenReturn(List.of(TEST_PRODUCT));

        // Act
        List<Product> products = productController.getProducts(httpServletRequest);

        // Assert
        assertThat(products).containsExactly(TEST_PRODUCT);
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProduct_returnsProduct() {
        // Act
        ResponseEntity<Product> response = productController.getProduct(PRODUCT_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(TEST_PRODUCT);
        verify(productService, times(1)).getProduct(PRODUCT_ID);
    }

    @Test
    void getProductsWithSorting_returnsPagedProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(TEST_PRODUCT), pageable, 1);
        when(productService.getProductsWithSorting(pageable)).thenReturn(productPage);

        // Act
        ResponseEntity<Page<Product>> response = productController.getProductsWithSorting(pageable);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).containsExactly(TEST_PRODUCT);
        verify(productService, times(1)).getProductsWithSorting(pageable);
    }

    @Test
    void addProduct_createsProduct() {
        // Arrange
        when(productService.addProduct(TEST_PRODUCT_DTO)).thenReturn(PRODUCT_ID);

        // Act
        ResponseEntity<Product> response = productController.addProduct(TEST_PRODUCT_DTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create("/products/" + PRODUCT_ID));
        verify(productService, times(1)).addProduct(TEST_PRODUCT_DTO);
    }

    @Test
    void patchProduct_updatesPartialProduct() {
        // Arrange
        when(productService.updateProduct(PRODUCT_ID, TEST_PRODUCT_DTO)).thenReturn(TEST_PRODUCT);

        // Act
        ResponseEntity<Product> response = productController.patchProduct(PRODUCT_ID, TEST_PRODUCT_DTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(TEST_PRODUCT);
        verify(productService, times(1)).updateProduct(PRODUCT_ID, TEST_PRODUCT_DTO);
    }

    @Test
    void updateProduct_updatesProduct() {
        // Arrange
        when(productService.updateProduct(PRODUCT_ID, TEST_PRODUCT_DTO)).thenReturn(TEST_PRODUCT);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.updateProduct(PRODUCT_ID, TEST_PRODUCT_DTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Product successfully updated");
        assertThat(response.getBody()).containsEntry("product", TEST_PRODUCT);
        verify(productService, times(1)).updateProduct(PRODUCT_ID, TEST_PRODUCT_DTO);
    }

    @Test
    void deleteProduct_removesProduct() {
        // Act
        ResponseEntity<Void> response = productController.deleteProduct(PRODUCT_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productService, times(1)).removeProduct(PRODUCT_ID);
    }
}
