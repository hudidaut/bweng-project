package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.ProductDto;
import at.technikum.springrestbackend.entity.Product;
import at.technikum.springrestbackend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all products
    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    // GET a single product by ID
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable UUID id) {
        return productService.getProduct(id);
    }

    // POST to add a new product
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody @Valid ProductDto productDto) {
        UUID uuid = productService.addProduct(productDto);
        return ResponseEntity
                .created(URI.create("/products/" + uuid))
                .build();
    }

    // PATCH to update part of a product
    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(@PathVariable UUID id,
                                                @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    // PUT to update a product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID id,
                                                 @RequestBody @Valid ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    // DELETE a product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.removeProduct(id);
        return ResponseEntity.ok().build();
    }
}
