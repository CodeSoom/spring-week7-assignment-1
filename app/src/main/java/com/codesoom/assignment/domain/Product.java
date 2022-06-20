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

    // TODO: 기존 데이터가 매개변수의 데이터로 수정되어야 한다.
    /**
     * Product 정보를 수정한다.
     *
     * @param newName 수정할 이름
     * @param newMaker 수정할 메이커
     * @param newPrice 수정할 가격
     * @param newImageUrl 수정할 이미지 URL
     */
    public void update(String newName, String newMaker, int newPrice, String newImageUrl) {
        this.name = newName;
        this.maker = newMaker;
        this.price = newPrice;
        this.imageUrl = newImageUrl;
    }
}
