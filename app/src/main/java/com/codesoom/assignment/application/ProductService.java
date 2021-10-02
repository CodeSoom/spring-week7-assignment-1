package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.InvalidRoleException;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final Mapper mapper;
    private final ProductRepository productRepository;

    public ProductService(
            Mapper dozerMapper,
            ProductRepository productRepository
    ) {
        this.mapper = dozerMapper;
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return findProduct(id);
    }

    public Product createProduct(ProductData productData, Authentication authentication) {
        Product product = mapper.map(productData, Product.class);
        product.setCreator(mapper.map(authentication, User.class));

        return productRepository.save(product);
    }

    /**
     * 상품을 수정하고 수정된 정보를 리턴한다.
     *
     * @param id 수정할 상품의 식별자
     * @param productData 수정할 상품의 목표 정보
     * @param authentication 권한 정보
     * @return 수정된 상품 정보
     * @Throw 권한이 없을 경우
     */
    public Product updateProduct(Long id, ProductData productData, Authentication authentication) {
        Product product = findProduct(id);

        if(!product.isAuthenticated(authentication)){
            throw new InvalidRoleException();
        };

        product.changeWith(mapper.map(productData, Product.class));

        return product;
    }

    public Product deleteProduct(Long id) {
        Product product = findProduct(id);

        productRepository.delete(product);

        return product;
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

}
