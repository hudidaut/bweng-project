package at.technikum.springrestbackend.repository;

import at.technikum.springrestbackend.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {
    @Override
    List<Product> findAll();
}

