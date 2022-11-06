package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 변경 서비스.
 */
@Service
@Transactional
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;

    public ProductCommandServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품을 생성합니다.
     * @param productData 생성할 상품 정보
     * @return 생성한 상품
     */
    public Product create(ProductData productData) {
        return productRepository.save(productData.toEntity());
    }

    /**
     * 상품을 수정합니다.
     * @param id 수정할 상품 아이디
     * @param productData 수정할 상품 정보
     * @return 수정된 상품
     * @throws ProductNotFoundException 상품을 찾지 못한 경우
     */
    public Product update(Long id, ProductData productData) {
        return productRepository.findById(id)
                .map(product -> {
                    product.update(productData.getName(),
                            productData.getMaker(),
                            productData.getPrice(),
                            productData.getImageUrl());
                    return product;
                })
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * 상품을 삭제합니다.
     * @param id 삭제할 상품 아이디
     * @return 삭제된 상품
     * @throws ProductNotFoundException 상품을 찾지 못한 경우
     */
    public Product delete(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return product;
                }).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
