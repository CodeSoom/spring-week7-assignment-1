// REST
// /products -> Create, Read
// /products/{id} -> Read, Update, Delete

package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.dto.ProductCommand;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.dto.ProductDto;
import com.codesoom.assignment.dto.ProductDto.ProductInfo;
import com.codesoom.assignment.mapper.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    private final AuthenticationService authenticationService;

    private final ProductMapper productMapper;

    public ProductController(ProductService productService,
                             AuthenticationService authenticationService,
                             ProductMapper productMapper) {
        this.productService = productService;
        this.authenticationService = authenticationService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public List<ProductInfo> list() {
        return productService.getProducts()
                .stream()
                .map(ProductInfo::new)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public ProductInfo detail(@PathVariable Long id) {
        return new ProductInfo(productService.getProduct(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductInfo createProduct(
            @RequestAttribute Long userId,
            @RequestBody @Valid ProductDto.RegisterParam request
    ) {
        final ProductCommand.Register command = productMapper.of(request);
        return new ProductInfo(productService.createProduct(command));
    }

    @PatchMapping("{id}")
    public ProductInfo updateProduct(
            @RequestAttribute Long userId,
            @PathVariable Long id,
            @RequestBody @Valid ProductDto.RegisterParam request
    ) {
        final ProductCommand.Update command = productMapper.of(id, request);
        return new ProductInfo(productService.updateProduct(command));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct (
            @RequestAttribute Long userId,
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
    }
}
