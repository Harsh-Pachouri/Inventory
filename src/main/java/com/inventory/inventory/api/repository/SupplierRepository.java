package com.inventory.inventory.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.inventory.inventory.api.model.Supplier;
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

}
