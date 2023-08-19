package com.codesoom.assignment.controllers;

import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.codesoom.assignment.utils.TestHelper.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ProductController 클래스")
public class ProductAopTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.save(TEST_PRODUCT);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class update_메서드는 {


        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 상품을_수정할_권한이_없는_경우 {
            @Test
            @DisplayName("에러메시지를 반환한다")
            void It_returns_403_error() throws Exception {
                mockMvc.perform(patch("/products/1")
                                .header("Authorization", "Bearer " + OTHER_USER_VALID_TOKEN)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(objectMapper.writeValueAsString(UPDATE_PRODUCT_DATA)))
                        .andExpect(status().isForbidden())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class delete_메서드는 {


        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 상품을_삭제할_권한이_없는_경우 {
            @Test
            @DisplayName("에러메시지를 반환한다")
            void It_returns_403_error() throws Exception {
                mockMvc.perform(delete("/products/1")
                                .header("Authorization", "Bearer " + OTHER_USER_VALID_TOKEN)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                                .contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andExpect(status().isForbidden())
                        .andDo(print());
            }
        }
    }

}
