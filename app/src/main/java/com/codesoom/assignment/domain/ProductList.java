package com.codesoom.assignment.domain;

import com.codesoom.assignment.convertors.ViewSupplier;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 상품 정보 일급 컬렉션
 */
public class ProductList {
    private final List<Product> store = new ArrayList<>();

    private ProductList() {
    }

    public static ProductList newInstance() {
        return new ProductList();
    }

    public static ProductList from(List<Product> list) {
        final ProductList productList = newInstance();

        productList.addAll(list);

        return productList;
    }

    public void addAll(Iterable<Product> list) {
        for (Product product : list) {
            this.add(product);
        }
    }

    public void add(Product product) {
        store.add(product);
    }

    public <R> List<R> map(ViewSupplier<Product, R> supplier) {
        return store.stream().map(supplier::supply)
                .collect(toList());
    }

    public List<Product> getAll() {
        return new ArrayList<>(store);
    }

    public Product get(int index) {
        return store.get(index);
    }

    public void remove(Product product) {
        final int index = store.indexOf(product);
        if (index > -1) {
            this.remove(index);
        }
    }

    public void remove(int index) {
        store.remove(index);
    }

    public boolean isEmpty() {
        return store.isEmpty();
    }

    public int size() {
        return store.size();
    }
}
