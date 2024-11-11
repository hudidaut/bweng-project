package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.ProductDto;
import at.technikum.springrestbackend.entity.Product;
import at.technikum.springrestbackend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import at.technikum.springrestbackend.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get a single product by ID
    public Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    // Add a new product
    public UUID addProduct(ProductDto productDto) {
        Product product = new Product(
                productDto.name(),
                productDto.price(),
                productDto.description(),
                productDto.category(),
                productDto.stockQuantity()
        );
        productRepository.save(product);
        return product.getId();
    }

    // Update (patch) product by ID
    public Product updateProduct(UUID id, ProductDto productDto) {
        Product product = getProduct(id);
        if (productDto.name() != null) {
            product.setName(productDto.name());
        }
        if (productDto.price() != null) {
            product.setPrice(productDto.price());
        }
        if (productDto.description() != null) {
            product.setDescription(productDto.description());
        }
        if (productDto.category() != null) {
            product.setCategory(productDto.category());
        }
        if (productDto.stockQuantity() != null) {
            product.setStockQuantity(productDto.stockQuantity());
        }
        return productRepository.save(product);
    }

    // Remove product by ID
    public void removeProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }
}