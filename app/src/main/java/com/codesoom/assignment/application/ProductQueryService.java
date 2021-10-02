package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.errors.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 상품을 반환합니다.
 */
@Service
public class ProductQueryService {
    private final ProductRepository productRepository;

    public ProductQueryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품 목록을 리턴합니다.
     * @return 상품 목록
     */
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    /**
     * 해당 식별자의 상품을 리턴합니다.
     * @param id 상품 식별자
     * @return 상품
     * @throws ProductNotFoundException 해당 식별자의 상품을 찾을 수 없는 경우
     */
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
