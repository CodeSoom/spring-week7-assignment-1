package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.ProductQueryService;
import com.codesoom.assignment.domain.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final ProductQueryService productQueryService;

    public ProductController(ProductQueryService productQueryService) {
        this.productQueryService = productQueryService;
    }

    @GetMapping
    public List<Product> list() {
        return productQueryService.getProducts();
    }

    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productQueryService.getProduct(id);
    }
}
