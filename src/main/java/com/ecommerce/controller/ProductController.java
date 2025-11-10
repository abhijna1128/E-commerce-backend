package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }

    @PostMapping
    public ResponseEntity<Product> add(@RequestBody Product p) { return ResponseEntity.ok(productService.addProduct(p)); }

    @GetMapping
    public ResponseEntity<List<Product>> all(@RequestParam(required = false) String category) {
        if (category != null) return ResponseEntity.ok(productService.findByCategory(category));
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}") public ResponseEntity<Product> get(@PathVariable Long id) { return ResponseEntity.ok(productService.getProductById(id)); }

    @PutMapping("/{id}") public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product p) { return ResponseEntity.ok(productService.updateProduct(id, p)); }

    @DeleteMapping("/{id}") public ResponseEntity<String> delete(@PathVariable Long id) { productService.deleteProduct(id); return ResponseEntity.ok("Deleted"); }
}
