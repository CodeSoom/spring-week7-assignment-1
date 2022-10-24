package com.codesoom.assignment.application;

import com.codesoom.assignment.application.dto.ProductCommand;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.codesoom.assignment.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * 전체 상품 목록을 리턴한다.
     * @return 전체 상품 목록
     */
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    /**
     * 상품ID에 해당하는 상품을 리턴한다.
     * @param id 상품 ID
     * @return 검색된 상품
     * @throws ProductNotFoundException 상품을 찾지 못한 경우
     *
     */
    public Product getProduct(Long id) {
        return findProduct(id);
    }


    /**
     * 새로운 상품을 등록하고, 등록된 상품을 리턴한다.
     * @param command 신규 상품정보
     * @return 등록된 상품
     */
    public Product createProduct(ProductCommand.Register command) {
        final Product product = productMapper.toEntity(command);
        return productRepository.save(product);
    }

    /**
     * 상품을 수정하고, 수정된 상품을 리턴한다.
     * @param command 수정 상품정보
     * @return 수정된 상품
     * @throws ProductNotFoundException 상품을 찾지 못한 경우
     */
    public Product updateProduct(ProductCommand.Update command) {
        final Product product = findProduct(command.getId());

        product.modifyProduct(productMapper.toEntity(command));

        return product;
    }

    /**
     * 상품을 삭제하고, 삭제된 상품을 리턴한다.
     * @param id 삭제할 상품 ID
     * @return 삭제된 상품
     * @throws ProductNotFoundException 상품을 찾지 못한 경우
     */
    public Product deleteProduct(Long id) {
        final Product product = findProduct(id);

        productRepository.delete(product);

        return product;
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
