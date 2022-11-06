package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;

/**
 * 상품 변경 서비스.
 */
public interface ProductCommandService {

    /**
     * 상품을 생성합니다.
     * @param productData 생성할 상품 정보
     * @return 생성한 상품
     */
    Product create(ProductData productData);

    /**
     * 상품을 수정합니다.
     * @param id 수정할 상품 아이디
     * @param productData 수정할 상품 정보
     * @return 수정된 상품
     */
    Product update(Long id, ProductData productData);

    /**
     * 상품을 삭제합니다.
     * @param id 삭제할 상품 아이디
     * @return 삭제된 상품
     */
    Product delete(Long id);
}
