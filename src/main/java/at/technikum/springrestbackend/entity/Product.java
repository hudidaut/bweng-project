package at.technikum.springrestbackend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private Double price;

    // Default constructor
    public Product() {}

    // Constructor with fields
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

