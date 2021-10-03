package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.ProductCommandService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 인증된 사용자의 상품에 대한 HTTP 요청을 처리합니다.
 */
@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductAdminController {
    private final ProductCommandService productCommandService;

    public ProductAdminController(ProductCommandService productCommandService) {
        this.productCommandService = productCommandService;
    }

    /**
     * 상품을 생성하고 리턴합니다.
     * @param productData 상품 생성 정보
     * @return 생성된 상품
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public Product create(
            @RequestBody @Valid ProductData productData
    ) {
        return productCommandService.createProduct(productData);
    }

    /**
     * 요청한 상품을 수정하고 리턴합니다.
     * @param id 상품 식별자
     * @param productData 상품 수정 정보
     * @return 수정된 상품
     */
    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public Product update(
            @PathVariable Long id,
            @RequestBody @Valid ProductData productData
    ) {
        return productCommandService.updateProduct(id, productData);
    }

    /**
     * 요청한 상품을 삭제합니다.
     * @param id 상품 식별자
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void destroy(
            @PathVariable Long id
    ) {
        productCommandService.deleteProduct(id);
    }
}
