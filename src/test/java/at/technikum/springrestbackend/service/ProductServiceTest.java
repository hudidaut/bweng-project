package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.ProductDto;
import at.technikum.springrestbackend.entity.Product;
import at.technikum.springrestbackend.exception.ResourceNotFoundException;
import at.technikum.springrestbackend.repository.ProductRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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
        lenient().when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(TEST_PRODUCT));
        lenient().when(productRepository.findAll()).thenReturn(List.of(TEST_PRODUCT));
        lenient().when(productRepository.existsById(PRODUCT_ID)).thenReturn(true);
    }

    @Test
    void getAllProducts_returnsProductList() {
        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
        assertThat(products).containsExactly(TEST_PRODUCT);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProduct_returnsProduct() {
        // Act
        Product product = productService.getProduct(PRODUCT_ID);

        // Assert
        assertThat(product).isEqualTo(TEST_PRODUCT);
        verify(productRepository, times(1)).findById(PRODUCT_ID);
    }

    @Test
    void getProduct_throwsResourceNotFoundException() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        when(productRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> productService.getProduct(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
    }

    @Test
    void getProductsWithSorting_returnsPagedProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(TEST_PRODUCT), pageable, 1);
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productService.getProductsWithSorting(pageable);

        // Assert
        assertThat(result.getContent()).containsExactly(TEST_PRODUCT);
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void addProduct_savesProductAndReturnsId() {
        // Arrange
        Product newProduct = new Product(
                "New Product",
                49.99,
                "New Description",
                "New Category",
                20,
                "http://example.com/new.jpg",
                "newuser",
                "newuser"
        );
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        // Act
        UUID result = productService.addProduct(TEST_PRODUCT_DTO);

        // Assert
        assertThat(result).isEqualTo(newProduct.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_updatesAndSavesProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(TEST_PRODUCT);

        // Act
        Product result = productService.updateProduct(PRODUCT_ID, TEST_PRODUCT_DTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(TEST_PRODUCT_DTO.name());
        assertThat(result.getPrice()).isEqualTo(TEST_PRODUCT_DTO.price());
        assertThat(result.getDescription()).isEqualTo(TEST_PRODUCT_DTO.description());
        assertThat(result.getCategory()).isEqualTo(TEST_PRODUCT_DTO.category());
        assertThat(result.getStockQuantity()).isEqualTo(TEST_PRODUCT_DTO.stockQuantity());
        assertThat(result.getImageUrl()).isEqualTo(TEST_PRODUCT_DTO.imageUrl());
        assertThat(result.getUpdatedByUserName()).isEqualTo(TEST_PRODUCT_DTO.updatedByUserName());

        verify(productRepository, times(1)).save(TEST_PRODUCT);
    }

    @Test
    void removeProduct_deletesProduct() {
        // Act
        productService.removeProduct(PRODUCT_ID);

        // Assert
        verify(productRepository, times(1)).deleteById(PRODUCT_ID);
    }

    @Test
    void removeProduct_throwsResourceNotFoundException() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        when(productRepository.existsById(unknownId)).thenReturn(false);

        // Assert
        assertThatThrownBy(() -> productService.removeProduct(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
    }
}
