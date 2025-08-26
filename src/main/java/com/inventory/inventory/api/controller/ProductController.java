package com.inventory.inventory.api.controller;

import com.inventory.inventory.api.dto.CreateProductRequest;
import com.inventory.inventory.api.model.Product;
import com.inventory.inventory.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findProductById(id);
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return this.productService.findAllProducts();
    }

    @PostMapping
    public Product createProduct(@Valid @RequestBody CreateProductRequest request){
        return this.productService.createProduct(request);
    }

}
