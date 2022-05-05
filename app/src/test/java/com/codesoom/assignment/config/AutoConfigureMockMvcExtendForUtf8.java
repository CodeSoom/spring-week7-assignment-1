package com.codesoom.assignment.config;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureMockMvc
@Import({
        AutoConfigureMockMvcExtendForUtf8.ExtendUtf8.class,
})
public @interface AutoConfigureMockMvcExtendForUtf8 {
    class ExtendUtf8{
        @Bean
        MockMvcBuilderCustomizer utf8Config() {
            return builder ->
                    builder.addFilters(new CharacterEncodingFilter("UTF-8", true));
        }
    }
}
