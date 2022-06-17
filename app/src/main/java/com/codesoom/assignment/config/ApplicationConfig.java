package com.codesoom.assignment.config;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    // TODO: Dozer Mapper를 Bean으로 등록해야 한다.
    @Bean
    public Mapper dozerMapper() {
        return DozerBeanMapperBuilder.buildDefault();
    }

    // TODO: PasswordEncoder를 Bean으로 등록해야 한다.
}
