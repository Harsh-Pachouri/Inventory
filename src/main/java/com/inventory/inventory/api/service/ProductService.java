package com.inventory.inventory.api.service;

import com.inventory.inventory.api.model.Product;
import com.inventory.inventory.api.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService{
    private final ProductRepository productRepository;

    @Cacheable(value = "products", key = "#id")
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    public Product createProduct(Product product){
        return this.productRepository.save(product);
    }
    public List<Product> findAllProducts(){
        return this.productRepository.findAll();
    }

}
