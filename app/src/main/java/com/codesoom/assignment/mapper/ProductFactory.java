package com.codesoom.assignment.mapper;

import com.codesoom.assignment.application.dto.ProductCommand;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductFactory {

    ProductFactory INSTANCE = Mappers.getMapper(ProductFactory.class);

    /**
     * 제품 Dto 객체를 제품등록 커맨드 객체로 변환한다.
     * @param request 제품 Dto
     * @return 제품등록 커맨드
     */
    ProductCommand.Register of(ProductDto.RegisterParam request);

    /**
     * 제품 ID와 제품 Dto 객체를 제품수정 커맨드 객체로 변환한다.
     * @param id 제품 ID
     * @param request 제품 Dto
     * @return 제품수정 커맨드
     */
    ProductCommand.Update of(Long id, ProductDto.RegisterParam request);

    /**
     * 제품등록 커맨드를 제품 엔티티로 변환한다.
     * @param command 제품등록 커맨드
     * @return 제품 엔티티
     */
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductCommand.Register command);

    /**
     * 제품수정 커맨드를 제품 엔티티로 변환한다.
     * @param command 제품수정 커맨드
     * @return 제품 엔티티
     */
    Product toEntity(ProductCommand.Update command);
}
