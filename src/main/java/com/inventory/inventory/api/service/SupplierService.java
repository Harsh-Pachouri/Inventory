package com.inventory.inventory.api.service;

import com.inventory.inventory.api.model.Product;
import com.inventory.inventory.api.model.Supplier;
import com.inventory.inventory.api.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository){
        this.supplierRepository = supplierRepository;
    }
    public Supplier createSupplier(Supplier supplier){
        return this.supplierRepository.save(supplier);
    }
    public List<Supplier> findAllSuppliers(){
        return this.supplierRepository.findAll();
    }
}
