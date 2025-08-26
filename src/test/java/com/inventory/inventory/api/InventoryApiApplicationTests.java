package com.inventory.inventory.api;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryApiApplicationTests extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private record Supplier(Long id, String name) {}
    private record Product(Long id, String name, int quantity, double price, Supplier supplier) {}
    private record CreateProductRequest(String name, int quantity, double price, Long supplierId) {}

    private static Long createdProductId;
    private static Long createdSupplierId;

    @Test
    @Order(1)
    void testCreateSupplierAndProduct() {
        // 1. Create a Supplier
        Supplier newSupplier = new Supplier(null, "Test Supplier");
        ResponseEntity<Supplier> supplierResponse = restTemplate.postForEntity("/api/suppliers", newSupplier, Supplier.class);
        assertEquals(HttpStatus.OK, supplierResponse.getStatusCode());
        assertNotNull(supplierResponse.getBody().id());
        createdSupplierId = supplierResponse.getBody().id();

        CreateProductRequest newProduct = new CreateProductRequest("Test Product", 100, 19.99, createdSupplierId);
        ResponseEntity<Product> productResponse = restTemplate.postForEntity("/api/products", newProduct, Product.class);
        assertEquals(HttpStatus.OK, productResponse.getStatusCode());
        assertNotNull(productResponse.getBody().id());
        assertEquals("Test Product", productResponse.getBody().name());
        assertEquals("Test Supplier", productResponse.getBody().supplier().name());
        createdProductId = productResponse.getBody().id();
    }

    @Test
    @Order(2)
    void testGetProductById() {
        assertNotNull(createdProductId, "Product ID should have been set by the create test");

        ResponseEntity<Product> response = restTemplate.getForEntity("/api/products/" + createdProductId, Product.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdProductId, response.getBody().id());
        assertEquals("Test Product", response.getBody().name());
        assertEquals(createdSupplierId, response.getBody().supplier().id());
    }
}