package com.codesoom.assignment.dto;

import com.codesoom.assignment.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductData {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String maker;

    @NotNull
    private Integer price;

    private String imageUrl;

    public Product toProduct(){
        Product.ProductBuilder productBuilder = Product.builder()
                                .id(this.id)
                                .name(this.name)
                                .price(this.price);

        if(StringUtils.hasText(this.imageUrl)){
           productBuilder.imageUrl(this.imageUrl);
        }
        return productBuilder.build();
    }
}
