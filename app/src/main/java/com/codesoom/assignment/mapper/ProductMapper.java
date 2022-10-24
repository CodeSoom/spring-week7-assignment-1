package com.codesoom.assignment.mapper;

import com.codesoom.assignment.application.ProductCommand;
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
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductCommand.Register of(ProductDto.RegisterParam request);

    ProductCommand.Update of(Long id, ProductDto.RegisterParam request);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductCommand.Register command);

    Product toEntity(ProductCommand.Update command);
}
