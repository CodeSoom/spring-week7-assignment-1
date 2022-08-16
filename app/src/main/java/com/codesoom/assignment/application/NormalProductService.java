package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.dto.ProductData;
import org.springframework.stereotype.Service;

@Service
public class NormalProductService implements ProductService {
    private final ProductRepository productRepository;

    public NormalProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product register(ProductData data) {
        return productRepository.save(data.toProduct());
    }
}
