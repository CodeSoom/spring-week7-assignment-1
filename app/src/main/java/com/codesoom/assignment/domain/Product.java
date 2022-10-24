package com.codesoom.assignment.domain;

import com.codesoom.assignment.errors.InvalidParamException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@ToString(of = {"id", "name", "maker", "price", "imageUrl"})
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    private String name;

    private String maker;

    private Integer price;

    private String imageUrl;

    protected Product() {
    }

    @Builder
    public Product(
            Long id,
            String name,
            String maker,
            Integer price,
            String imageUrl
    ) {
        if (Strings.isBlank(name)) {
            throw new InvalidParamException("이름이 비어있습니다.");
        }

        if (Strings.isBlank(maker)) {
            throw new InvalidParamException("제조사가 비어있습니다.");
        }

        if (price == null) {
            throw new InvalidParamException("가격이 비어있습니다.");
        }

        this.id = id;
        this.name = name;
        this.maker = maker;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void modifyProduct(Product source) {
        this.name = source.name;
        this.maker = source.maker;
        this.price = source.price;
        this.imageUrl = source.imageUrl;
    }
}
