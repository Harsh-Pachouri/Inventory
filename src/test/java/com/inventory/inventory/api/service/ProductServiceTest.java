package com.inventory.inventory.api.service;

import com.inventory.inventory.api.model.Product;
import com.inventory.inventory.api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void testFindAllProducts(){
        Product product1 = new Product("Razer Viper Mini", 1, 30.0);
        List<Product> productList = List.of(product1);
        when(productRepository.findAll()).thenReturn(productList);

        List<Product> result = productService.findAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Razer Viper Mini", result.get(0).getName());
    }
}