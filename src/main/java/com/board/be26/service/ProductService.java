package com.board.be26.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.be26.dto.CreateProductRequest;
import com.board.be26.dto.ProductResponse;
import com.board.be26.dto.UpdateProductRequest;
import com.board.be26.entity.Product;
import com.board.be26.entity.ProductStatus;
import com.board.be26.repositories.ProductRepository;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest product) {
        logger.info("Creating a new product: {}", product.getName());

        productRepository.findBySku(product.getSku())
                .ifPresent(p -> { throw new IllegalArgumentException("SKU already exists"); });

        Product newProduct = new Product();
        newProduct.setSku(product.getSku());
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_UP));
        newProduct.setStock(product.getStock());
        newProduct.setCategory(product.getCategory());
        newProduct.setStatus(ProductStatus.ACTIVE);

        Product saved = productRepository.save(newProduct);
        return toResponse(saved);
    }

    public Optional<Product> findEntityById(Long id) {
        return productRepository.findById(id);
    }

    public ProductResponse getProductByName(String name) {
        logger.info("Fetching product by name: {}", name);
        return productRepository.findByName(name).map(this::toResponse).orElse(null);
    }

    public ProductResponse getProductById(Long id) {
        logger.info("Fetching product by ID: {}", id);
        return productRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public Page<ProductResponse> searchProducts(String nameContains, String category, ProductStatus status, int page, int size, String sortBy, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Product> pageResult = productRepository.search(status, category, nameContains, pageable);
        return pageResult.map(this::toResponse);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (request.getSku() != null) {
            productRepository.findBySku(request.getSku())
                    .filter(p -> !p.getId().equals(id))
                    .ifPresent(p -> { throw new IllegalArgumentException("SKU already exists"); });
            product.setSku(request.getSku());
        }
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice().setScale(2, RoundingMode.HALF_UP));
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void archiveProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setStatus(ProductStatus.ARCHIVED);
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse resp = new ProductResponse();
        resp.setId(product.getId());
        resp.setSku(product.getSku());
        resp.setName(product.getName());
        resp.setDescription(product.getDescription());
        resp.setPrice(product.getPrice());
        resp.setStock(product.getStock());
        resp.setCategory(product.getCategory());
        resp.setStatus(product.getStatus());
        resp.setCreatedAt(product.getCreatedAt());
        resp.setUpdatedAt(product.getUpdatedAt());
        return resp;
    }
}
