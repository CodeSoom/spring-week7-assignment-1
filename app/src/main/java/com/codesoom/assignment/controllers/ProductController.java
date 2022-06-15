// REST
// /products -> Create, Read
// /products/{id} -> Read, Update, Delete

package com.codesoom.assignment.controllers;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.InvalidTokenException;

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
	@ResponseStatus(HttpStatus.CREATED)
	public Product create(
		@AuthenticationPrincipal Principal principal,
		@RequestBody @Valid ProductData productData
	) {
		if (principal == null) {
			throw new InvalidTokenException("null");
		}
		return productService.createProduct(productData);
	}

	@PatchMapping("{id}")
	public Product update(
		@AuthenticationPrincipal Principal principal,
		@PathVariable Long id,
		@RequestBody @Valid ProductData productData
	) {
		if (principal == null) {
			throw new InvalidTokenException("null");
		}
		return productService.updateProduct(id, productData);
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void destroy(
        @AuthenticationPrincipal Principal principal,
		@PathVariable Long id
	) {
		productService.deleteProduct(id);
	}
}
