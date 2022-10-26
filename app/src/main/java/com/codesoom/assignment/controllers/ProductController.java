package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.application.dto.ProductCommand;
import com.codesoom.assignment.dto.ProductDto;
import com.codesoom.assignment.dto.ProductDto.ProductInfo;
import com.codesoom.assignment.mapper.ProductFactory;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    private final ProductFactory productMapper;

    public ProductController(ProductService productService,
                             ProductFactory productMapper) {
        this.productService = productService;
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
    @PreAuthorize("isAuthenticated()")
    public ProductInfo createProduct(
            @RequestBody @Valid ProductDto.RegisterParam request,
            Authentication authentication
    ) {
        System.out.println("adsfasfasd");
        System.out.println("authentication = " + authentication.getPrincipal());
        final ProductCommand.Register command = productMapper.of(request);
        return new ProductInfo(productService.createProduct(command));
    }

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ProductInfo updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductDto.RegisterParam request,
            Authentication authentication
    ) {
        final ProductCommand.Update command = productMapper.of(id, request);
        return new ProductInfo(productService.updateProduct(command));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deleteProduct (
            @PathVariable Long id,
            Authentication authentication
    ) {
        productService.deleteProduct(id);
    }
}
