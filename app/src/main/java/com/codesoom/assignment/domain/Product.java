// 고양이 장난감 쇼핑몰
// Product 모델
// User 모델
// Order 모델
// ... 모델
// Application (UseCase)
// Product -> 관리자 등록/수정/삭제 -> list/detail
// 주문 -> 확인 -> 배송 등 처리

// Product
// 0. 식별자 - identifier (ID)
// 1. 이름 - 쥐돌이
// 2. 제조사 - 냥이월드
// 3. 가격 - 5,000원 (판매가)
// 4. 이미지 - static, CDN => image URL

package com.codesoom.assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User creator;

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void changeWith(Product source) {
        this.name = source.name;
        this.maker = source.maker;
        this.price = source.price;
        this.imageUrl = source.imageUrl;
    }

    /**
     * 상품에 대한 접근 권한이 있으면 true 없으면 false를 리턴합니다.
     *
     * @param authentication 권한 정보
     * @return 1. 관리자 권한 2. 상품을 생성한 사용자 : true 리턴
     *         1. 관자자 권한이 없는 경우 2. 상품을 생성한 사용자가 아닌 경우 : false 리턴
     */
    public boolean isAuthenticated(Authentication authentication) {
        return authentication.getAuthorities().stream().filter(auth -> auth.getAuthority().equals("ADMIN")).count() > 0
                || authentication.getPrincipal().equals(this.getCreator().getId());
    }
}
