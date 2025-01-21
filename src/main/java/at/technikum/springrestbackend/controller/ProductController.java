package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.ProductDto;
import at.technikum.springrestbackend.entity.Product;
import at.technikum.springrestbackend.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all products
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Product> getProducts(HttpServletRequest request) {
        System.out.println("Authorization Header: " + request.getHeader("Authorization"));
        return productService.getAllProducts();
    }

    // GET a single product by ID
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Product> getProduct(@PathVariable UUID id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    // GET product sorted by a field
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/sorted")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Product>> getProductsWithSorting(Pageable pageable) {
        Page<Product> products = productService.getProductsWithSorting(pageable);
        return ResponseEntity.ok(products);
    }


    // POST to add a new product
    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid ProductDto productDto) {
        UUID uuid = productService.addProduct(productDto);
        return ResponseEntity.created(URI.create("/products/" + uuid)).build();
    }

    // PATCH to update part of a product
    @CrossOrigin(origins = "http://localhost:8081")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> patchProduct(@PathVariable UUID id,
                                                @RequestBody ProductDto productDto) {
        Product updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    // PUT to update a product
    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable UUID id,
            @RequestBody @Valid ProductDto productDto) {

        Product updatedProduct = productService.updateProduct(id, productDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product successfully updated");
        response.put("product", updatedProduct);

        return ResponseEntity.ok(response);
    }


    // DELETE a product by ID
    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.removeProduct(id);
        return ResponseEntity.noContent().build();
    }
}