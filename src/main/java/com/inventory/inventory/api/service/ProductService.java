package com.inventory.inventory.api.service;

import com.inventory.inventory.api.dto.CreateProductRequest;
import com.inventory.inventory.api.model.Product;
import com.inventory.inventory.api.model.Supplier;
import com.inventory.inventory.api.repository.ProductRepository;
import com.inventory.inventory.api.repository.SupplierRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService{
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository, SupplierRepository supplierRepository){
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    @Cacheable(value = "products", key = "#id")
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product createProduct(CreateProductRequest request) {
        Supplier supplier = supplierRepository.findById(request.supplierId())
            .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + request.supplierId()));
        
        Product product = new Product(request.name(), request.quantity(), request.price());
        product.setSupplier(supplier);
        
        return this.productRepository.save(product);
    }

    public Product createProduct(Product product){
        return this.productRepository.save(product);
    }

    public List<Product> findAllProducts(){
        return this.productRepository.findAll();
    }

}
