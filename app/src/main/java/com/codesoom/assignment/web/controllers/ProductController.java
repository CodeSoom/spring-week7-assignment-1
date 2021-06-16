// REST
// /products -> Create, Read
// /products/{id} -> Read, Update, Delete

package com.codesoom.assignment.web.controllers;

import com.codesoom.assignment.core.application.AuthenticationService;
import com.codesoom.assignment.core.application.ProductService;
import com.codesoom.assignment.core.domain.Product;
import com.codesoom.assignment.web.dto.ProductData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 제품에 대한 요청를 처리하고 응답합니다.
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
     * 모든 제품 목록을 반환합니다.
     * @return 제품 목록
     */
    @GetMapping
    public List<Product> list() {
        return productService.getProducts();
    }

    /**
     * 요청 식별자에 해당하는 제품을 반환합니다.
     * @param id 제품 식별자
     * @return 제품
     */
    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    /**
     * 요청 제품을 등록하고, 등록한 제품을 반환합니다.
     * @param productData 신규 등록할 제품
     * @return 등록한 제품
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Product create(@RequestBody @Valid ProductData productData) {
        return productService.createProduct(productData);
    }

    /**
     * 제품 정보를 갱신하고, 갱신한 제품을 반환합니다.
     * @param id 제품 식별자
     * @param productData 갱신할 제품
     * @return 갱신한 제품
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
     * 제품을 삭제합니다.
     * @param id 제품 식별자
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void destroy(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
