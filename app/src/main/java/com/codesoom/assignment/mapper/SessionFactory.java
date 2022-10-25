package com.codesoom.assignment.mapper;

import com.codesoom.assignment.application.dto.SessionCommand;
import com.codesoom.assignment.dto.SessionDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SessionFactory {

    SessionFactory INSTANCE = Mappers.getMapper(SessionFactory.class);

    /**
     * 세션 Dto 객체를 세션 커맨드 객체로 변환한다.
     * @param request 세션 Dto
     * @return 세션 커맨드
     */
    SessionCommand.SessionRequest of(SessionDto.SessionRequestData request);

}
