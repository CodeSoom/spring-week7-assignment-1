package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;

import java.util.List;

/**
 * 상품 조회 서비스.
 */
public interface ProductQueryService {

    /**
     * 모든 상품을 조회합니다.
     * @return 모든 상품
     */
    List<Product> getAll();

    /**
     * 상품을 조회합니다.
     * @param id 조회할 상품 정보
     * @return 상품
     */
    Product get(Long id);
}
