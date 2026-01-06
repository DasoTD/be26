package com.board.be26.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.board.be26.dto.CreateProductRequest;
import com.board.be26.entity.Product;
import com.board.be26.repositories.ProductRepository;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    public void someProductServiceMethod() {
        logger.info("Executing some product service method.");
        // Business logic here
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public Product createProduct(CreateProductRequest product) {
        logger.info("Creating a new product: {}", product.getName());
        Product newProduct = new Product();
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setStock(product.getStock());
        return productRepository.save(newProduct);
    }

    public Product getProductByName(String name) {
        logger.info("Fetching product by name: {}", name);
        return productRepository.findByName(name)
                .orElse(null);
    }

    public Product getProductById(Long id) {
        logger.info("Fetching product by ID: {}", id);
        return productRepository.findById(id).orElse(null);
    }

    public Iterable<Product> getAllProducts() {
        logger.info("Fetching all products.");
        return productRepository.findAll();
    }

    public void updateProductStock(String name, int newStock) {
        logger.info("Updating stock for product: {} to {}", name, newStock);
        Product product = getProductByName(name);
        if (product != null) {
            product.setStock(newStock);
            productRepository.save(product);
        } else {
            logger.warn("Product with name: {} not found.", name);
        }
    }

    public void deleteProductByName(String name) {
        logger.info("Deleting product by name: {}", name);
        Product product = getProductByName(name);
        if (product != null) {
            productRepository.delete(product);
        } else {
            logger.warn("Product with name: {} not found.", name);
        }
    }

    
}
