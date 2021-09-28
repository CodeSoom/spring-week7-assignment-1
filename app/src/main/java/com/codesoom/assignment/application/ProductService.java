package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductList;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.InvalidProductArgumentsException;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.codesoom.assignment.suppliers.EntitySupplier;
import com.codesoom.assignment.validators.Validators;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Service;

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

    public ProductList getProducts() {
        return ProductList.from(productRepository.findAll());
    }

    public Product getProduct(Long id) {
        return findProduct(id);
    }

    public Product createProduct(EntitySupplier<Product> supplier) {
        validOrThrow(supplier);

        final Product product = supplier.toEntity();

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, EntitySupplier<Product> supplier) {
        validOrThrow(supplier);

        Product product = findProduct(id);

        product.changeWith(supplier.toEntity());

        return product;
    }

    private void validOrThrow(EntitySupplier<Product> supplier) {
        final List<String> validateResult = Validators.validate(supplier);

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
