package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.ProductQueryService;
import com.codesoom.assignment.domain.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 상품에 대한 HTTP 요청을 처리합니다.
 */
@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final ProductQueryService productQueryService;

    public ProductController(ProductQueryService productQueryService) {
        this.productQueryService = productQueryService;
    }

    /**
     * 상품 목록을 리턴합니다.
     * @return 상품목록
     */
    @GetMapping
    public List<Product> list() {
        return productQueryService.getProducts();
    }

    /**
     * 요청한 상품을 리턴합니다.
     * @param id 상품 식별자
     * @return 상품
     */
    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productQueryService.getProduct(id);
    }
}
