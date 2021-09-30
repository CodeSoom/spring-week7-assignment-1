package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductList;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.errors.InvalidProductArgumentsException;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.codesoom.assignment.suppliers.EntitySupplier;
import com.codesoom.assignment.validators.Validators;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 상품을 관리한다.
 */
@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductList getProducts() {
        return ProductList.from(productRepository.findAll());
    }

    public Product getProduct(Long id) {
        return findProduct(id);
    }

    public Product createProduct(EntitySupplier<Product> supplier) {
        checkValidOrThrow(supplier);

        final Product product = supplier.toEntity();

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, EntitySupplier<Product> supplier) throws InvalidProductArgumentsException{
        checkValidOrThrow(supplier);

        Product product = findProduct(id);

        product.changeWith(supplier.toEntity());

        return product;
    }

    private void checkValidOrThrow(EntitySupplier<Product> supplier) {
        final List<String> validateResult = Validators.getValidateResults(supplier);

        if (!validateResult.isEmpty()) {
            throw new InvalidProductArgumentsException(String.join(",", validateResult));
        }
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
