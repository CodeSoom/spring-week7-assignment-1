package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.errors.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 상품 조회 서비스.
 */
@Service
@Transactional(readOnly = true)
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    public ProductQueryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 모든 상품을 조회합니다.
     * @return 모든 상품
     */
    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    /**
     * 상품을 조회합니다.
     * @param id 조회할 상품 정보
     * @return 상품
     * @throws ProductNotFoundException 상품을 찾지 못한 경우
     */
    @Override
    public Product get(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
