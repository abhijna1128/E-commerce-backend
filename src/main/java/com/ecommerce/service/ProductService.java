package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(Product p) {
        return productRepository.save(p);
    }

    public Product updateProduct(Long id, Product updated) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (updated.getName() != null) p.setName(updated.getName());
        if (updated.getDescription() != null) p.setDescription(updated.getDescription());
        if (updated.getPrice() != null) p.setPrice(updated.getPrice());
        if (updated.getStock() != 0) p.setStock(updated.getStock());
        if (updated.getCategory() != null) p.setCategory(updated.getCategory());
        if (updated.getImageUrl() != null) p.setImageUrl(updated.getImageUrl());
        p.setRating(updated.getRating());
        return productRepository.save(p);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategoryContainingIgnoreCase(category);
    }
}
