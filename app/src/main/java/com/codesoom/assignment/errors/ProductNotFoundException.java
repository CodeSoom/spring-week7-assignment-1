package com.codesoom.assignment.errors;

/**
 * 제품을 찾을 수 없을 경우 던집니다.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found: " + id);
    }
}
