package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Service;

/**
 * 상품을 추가, 수정, 삭제하는 기능을 제공합니다.
 */
@Service
public class ProductCommandService {
    private final Mapper mapper;
    private final ProductRepository productRepository;

    public ProductCommandService(
            Mapper dozerMapper,
            ProductRepository productRepository
    ) {
        this.mapper = dozerMapper;
        this.productRepository = productRepository;
    }

    /**
     * 상품을 생성하고 리턴합니다.
     * @param productData 상품 생성 정보
     * @return 생성된 상품
     */
    public Product createProduct(ProductData productData) {
        Product product = mapper.map(productData, Product.class);
        return productRepository.save(product);
    }

    /**
     * 해당 식별자의 상품을 수정하고 리턴합니다.
     * @param id 상품 식별자
     * @param productData 상품 수정 정보
     * @return 수정된 상품
     */
    public Product updateProduct(Long id, ProductData productData) {
        Product product = findProduct(id);

        product.changeWith(mapper.map(productData, Product.class));

        return product;
    }

    /**
     * 해당 식별자의 상품을 삭제하고 리턴합니다.
     * @param id 상품 식별자
     * @return 삭제된 상품
     */
    public Product deleteProduct(Long id) {
        Product product = findProduct(id);

        productRepository.delete(product);

        return product;
    }

    /**
     * 해당 식별자의 상품을 리턴합니다.
     * @param id 상품 식별자
     * @return 상품
     * @throws ProductNotFoundException 해당 식별자의 상품을 찾지 못한 경우
     */
    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
