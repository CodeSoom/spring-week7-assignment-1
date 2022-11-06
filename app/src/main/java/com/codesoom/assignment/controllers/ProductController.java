package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductCommandService;
import com.codesoom.assignment.application.ProductQueryService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

    private final ProductQueryService productQueryService;
    private final ProductCommandService productCommandService;
    private final AuthenticationService authenticationService;

    public ProductController(ProductQueryService productQueryService,
                             ProductCommandService productCommandService,
                             AuthenticationService authenticationService) {
        this.productQueryService = productQueryService;
        this.productCommandService = productCommandService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public List<Product> list() {
        return productQueryService.getAll();
    }

    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productQueryService.get(id);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() && hasAuthority('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody @Valid ProductData productData) {
        return productCommandService.create(productData);
    }

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated() && hasAuthority('USER')")
    public Product update(@PathVariable Long id, @RequestBody @Valid ProductData productData) {
        return productCommandService.update(id, productData);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated() && hasAuthority('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        productCommandService.delete(id);
    }
}
