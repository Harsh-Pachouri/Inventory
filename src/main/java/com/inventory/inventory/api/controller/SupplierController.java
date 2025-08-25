package com.inventory.inventory.api.controller;

import com.inventory.inventory.api.model.Product;
import com.inventory.inventory.api.model.Supplier;
import com.inventory.inventory.api.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    final SupplierService supplierService;

    public SupplierController(SupplierService supplierService){
        this.supplierService = supplierService;
    }

    @GetMapping
    public List<Supplier> getAllSuppliers(){
        return this.supplierService.findAllSuppliers();
    }

    @PostMapping
    public Supplier createSupplier(@Valid @RequestBody Supplier supplier){
        return this.supplierService.createSupplier(supplier);
    }
}
