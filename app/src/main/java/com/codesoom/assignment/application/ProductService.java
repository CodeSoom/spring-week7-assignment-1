package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import org.springframework.security.core.Authentication;

public interface ProductService {
    /**
     * 상품을 생성하고 리턴한다.
     * @param data 상품 정보
     * @param authentication 유저 정보
     * @return 상품
     */
    Product register(ProductData data, Authentication authentication);
}
