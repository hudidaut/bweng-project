package at.technikum.springrestbackend.repository;

import at.technikum.springrestbackend.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {
    @Override
    @NonNull
    List<Product> findAll();
}
