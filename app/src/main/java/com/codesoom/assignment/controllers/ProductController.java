// REST
// /products -> Create, Read
// /products/{id} -> Read, Update, Delete

package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductList;
import com.codesoom.assignment.dto.ProductData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 상품 정보에 관련된 HTTP Request를 처리한다.
 */
@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 상품 목록을 조회한다.
     *
     * @return 상품 목록
     */
    @GetMapping
    public List<ProductData> list() {
        final ProductList products = productService.getProducts();
        return products.convert(ProductData::from);
    }

    /**
     * 상품 정보를 조회한다.
     *
     * @param id 상품 식별자
     * @return 상품 상세 정보
     */
    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public Product create(
            @RequestBody @Valid ProductData productData,
            Authentication authentication
    ) {
        return productService.createProduct(productData);
    }

    /**
     * 상품 정보를 수정한다.
     *
     * @param id 상품 식별자
     * @param productData    수정 할 상품 정보
     * @param authentication 인증 정보
     * @return 수정된 상품 정보
     */
    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public Product update(
            @PathVariable Long id,
            @RequestBody @Valid ProductData productData,
            Authentication authentication
    ) {
        return productService.updateProduct(id, productData);
    }

    /**
     * 상품을 삭제한다.
     *
     * @param id 상품 식별자
     * @param authentication 인증 정보
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void destroy(
            @PathVariable Long id,
            Authentication authentication
    ) {
        productService.deleteProduct(id);
    }
}
