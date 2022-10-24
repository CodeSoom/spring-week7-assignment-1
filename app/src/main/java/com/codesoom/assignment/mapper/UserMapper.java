package com.codesoom.assignment.mapper;

import com.codesoom.assignment.application.UserCommand;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserDto;
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
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserCommand.Register of(UserDto.RegisterParam request);

    UserCommand.Update of(Long id, UserDto.UpdateParam request);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserCommand.Register command);

    @Mapping(target = "email", ignore = true)
    User toEntity(UserCommand.Update command);
}
