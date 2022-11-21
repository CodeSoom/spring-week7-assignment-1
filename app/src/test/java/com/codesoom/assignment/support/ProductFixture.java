package com.codesoom.assignment.support;

import com.codesoom.assignment.product.domain.Product;
import com.codesoom.assignment.product.presentation.dto.ProductData;

public enum ProductFixture {
    PRODUCT_1("쥐돌이", "냥이월드", 5000, ""),
    PRODUCT_2("범냐옹", "메이드인 안양", 3000000, "https://avatars.githubusercontent.com/u/59248326"),
    PRODUCT_INVALID_NAME("", "이름이 없지롱", 200, ""),
    PRODUCT_INVALID_MAKER("메이커가 없지롱", "", 200, ""),
    PRODUCT_INVALID_PRICE("가격이 음수지롱", "가격이 마이너스지롱", -20000, ""),
    ;

    private final String name;
    private final String maker;
    private final int price;
    private final String imageUrl;

    ProductFixture(String name, String maker, int price, String imageUrl) {
        this.name = name;
        this.maker = maker;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product 엔티티_생성() {
        return 엔티티_생성(null);
    }

    public Product 엔티티_생성(final Long id) {
        return Product.builder()
                .id(id)
                .name(name)
                .maker(maker)
                .price(price)
                .imageUrl(imageUrl)
                .build();
    }

    public ProductData 등록_요청_데이터_생성() {
        return ProductData.builder()
                .name(name)
                .maker(maker)
                .price(price)
                .imageUrl(imageUrl)
                .build();
    }

    public ProductData 수정_요청_데이터_생성() {
        return ProductData.builder()
                .name(name)
                .maker(maker)
                .price(price)
                .imageUrl(imageUrl)
                .build();
    }

    public String 이름() {
        return name;
    }

    public String 메이커() {
        return maker;
    }

    public int 가격() {
        return price;
    }

    public String 이미지_URL() {
        return imageUrl;
    }
}
