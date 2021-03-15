// REST
// /products -> Create, Read
// /products/{id} -> Read, Update, Delete

package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 상품에 대한 HTTP 요청 핸들러.
 */
@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    private final AuthenticationService authenticationService;

    public ProductController(ProductService productService,
                             AuthenticationService authenticationService) {
        this.productService = productService;
        this.authenticationService = authenticationService;
    }

    /**
     * 존재하는 모든 상품 목록을 응답한다.
     *
     * @return 상품 목록
     */
    @GetMapping
    public List<Product> list() {
        return productService.getProducts();
    }

    /**
     * 주어진 id와 일치하는 상품을 응답한다.
     *
     * @param id 상품 식별자
     * @return 주어진 id와 일치하는 상품
     */
    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    /**
     * 주어진 상품 정보로 새로운 상품을 생성한 뒤 응답한다.
     *
     * @param productData 상품 정보
     * @return 생성된 상품
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public Product create(@RequestBody @Valid ProductData productData) {
        return productService.createProduct(productData);
    }

    /**
     * 주어진 id와 일치하는 상품을 주어진 상품 정보로 수정한 뒤 응답한다.
     *
     * @param id          상품 식별자
     * @param productData 상품 수정 정보
     * @return 수정된 상품
     */
    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public Product update(
            @PathVariable Long id,
            @RequestBody @Valid ProductData productData
    ) {
        return productService.updateProduct(id, productData);
    }

    /**
     * 주어진 id와 일치하는 상품을 삭제한다.
     *
     * @param id 상품 식별자
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void destroy(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
    }
}
