package com.codesoom.assignment;

import com.codesoom.assignment.domain.Product;

/**
 * 상품 테스트를 위한 상수 모음
 */
public interface ConstantsForProductTest {
    String PRODUCT_NAME = "장난감 뱀";
    String PRODUCT_MAKER = "고양이 주식회사";
    Integer PRODUCT_PRICE = 5000;
    String PRODUCT_IMAGE_URL = "https://sc04.alicdn.com/kf/HTB10TD8ipmWBuNjSspdq6zugXXag.jpg";

    Product 츄르 = new Product("츄르",
            "고양이 주식회사",
            3000,
            "https://sc04.alicdn.com/kf/HTB10TD8ipmWBuNjSspdq6zugXXag.jpg");

    Product 스크래쳐 = new Product("스크래쳐",
            "고양이 주식회사",
            15000,
            "http://image.auction.co.kr/itemimage/18/90/d6/1890d6a786.jpg");

    Product 펫모닝 = new Product("펫모닝",
            "고양이 주식회사",
            4500,
            "https://static19.fitpetmall.co.kr/modify-images/h/750/w/750/q/95/p/images/" +
                    "product/cover/e1/00/e100bab3513945cd993334b7ffa027a4.png");

    Product 두더지잡기 = new Product("두더지잡기",
            "고양이 주식회사",
            35000,
            "http://ohpackege.com/web/upload/NNEditor/20190113/1_shop1_170202.jpg");
}
