// REST
// /products -> Create, Read
// /products/{id} -> Read, Update, Delete

package com.codesoom.assignment.controllers;

import com.codesoom.assignment.aop.annotation.CheckOwner;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.getProducts();
    }

    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Product create(
            @RequestBody @Valid ProductData productData,
            Authentication authentication
    ) {
        return productService.createProduct(productData, (Long)authentication.getPrincipal());
    }

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    @CheckOwner
    public Product update(
            @PathVariable Long id,
            @RequestBody @Valid ProductData productData
    ) {
        return productService.updateProduct(id, productData);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CheckOwner
    public void destroy(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
    }

}
