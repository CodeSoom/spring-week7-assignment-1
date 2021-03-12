// REST
// /products -> Create, Read
// /products/{id} -> Read, Update, Delete

package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping
    public List<Product> list() {
        return productService.getProducts();
    }

    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    // 누가 이걸 하는 거지? => authentication
    // 이건 로그인해야 해! => authorization
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(
            @RequestAttribute Long userId,
            @RequestBody @Valid ProductData productData,
            Authentication authentication
    ) {
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        System.out.println(authentication);
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        return productService.createProduct(productData);
    }

    @PatchMapping("{id}")
    // 누가 이걸 하는 거지? => authentication
    // 이건 로그인해야 해! => authorization
    public Product update(
            @RequestAttribute Long userId,
            @PathVariable Long id,
            @RequestBody @Valid ProductData productData
    ) {
        return productService.updateProduct(id, productData);
    }

    @DeleteMapping("{id}")
    // 누가 이걸 하는 거지? => authentication
    // 이건 로그인해야 해! => authorization
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(
            @RequestAttribute Long userId,
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
    }
}
