package com.board.be26.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.board.be26.dto.CreateProductRequest;
import com.board.be26.entity.Product;
import com.board.be26.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        Product createdProduct = productService.createProduct(createProductRequest);
        return ResponseEntity.status(201).body(createdProduct);
    }

    @GetMapping
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        Iterable<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Product> getProductByName(@PathVariable String name) {
        Product product = productService.getProductByName(name);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{name}/stock")
    public ResponseEntity<Void> updateProductStock(@PathVariable String name, @RequestParam int stock) {
        productService.updateProductStock(name, stock);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteProductByName(@PathVariable String name) {
        productService.deleteProductByName(name);
        return ResponseEntity.ok().build();
    }
}