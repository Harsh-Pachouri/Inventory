package com.inventory.inventory.api.repository;

import com.inventory.inventory.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
