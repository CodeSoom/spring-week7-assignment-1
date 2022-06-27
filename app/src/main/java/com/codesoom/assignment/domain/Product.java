package com.codesoom.assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Product Entity 클래스
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String maker;

    private Integer price;

    private String imageUrl;

    /**
     * Product 정보를 수정한다.
     *
     * @param name 수정할 이름
     * @param maker 수정할 메이커
     * @param price 수정할 가격
     * @param imageUrl 수정할 이미지 URL
     */
    public void update(String name, String maker, int price, String imageUrl) {
        this.name = name;
        this.maker = maker;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}
