package com.codesoom.assignment;

public class Fixture {
    public static final String SESSION_PATH = "/session";
    public static final String USER_PATH = "/users";
    public static final String PRODUCT_PATH = "/products";
    public static final String EMAIL = "qjawlsqjacks@naver.com";
    public static final String PASSWORD = "1234";
    public static final String USER_NAME = "박범진";
    public static final String PRODUCT_NAME = "상품명";
    public static final Integer QUANTITY = 10;
    public static final Integer PRICE = 10000;

    private Fixture() {
        throw new IllegalStateException("테스트 Util 클래스입니다.");
    }
}
