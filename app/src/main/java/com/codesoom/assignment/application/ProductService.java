package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;

public interface ProductService {
    /**
     * 유저를 생성하고 리턴한다.
     * @param data 유저 정보
     * @return 유저
     */
    Product register(ProductData data);
}
