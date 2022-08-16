package com.codesoom.assignment.infra;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaProductRepository
        extends ProductRepository, CrudRepository<Product, Long> {
    Product save(Product product);
}
