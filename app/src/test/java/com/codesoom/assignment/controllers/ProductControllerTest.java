package com.codesoom.assignment.controllers;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.mapper.ProductFactory;
import com.codesoom.assignment.security.JwtTokenProvider;
import com.codesoom.assignment.utils.ProductSampleFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProductController 클래스")
class ProductControllerTest {
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ProductFactory productMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() throws Exception {
        // ResponseBody JSON에 한글이 깨지는 문제로 추가
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Nested
    @DisplayName("list[/products::GET] 메소드는")
    class Describe_list {
        ResultActions subject() throws Exception {
            return mockMvc.perform(get("/products"));
        }

        @Nested
        @DisplayName("상품이 존재하면")
        class Context_with_products {
            private List<Product> givenProducts = new ArrayList<>();

            @BeforeEach
            void prepare() {
                productRepository.save(ProductSampleFactory.createProduct());
                productRepository.save(ProductSampleFactory.createProduct());

                givenProducts = productRepository.findAll();
            }

            @Test
            @DisplayName("OK(200)와 모든 상품을 리턴한다")
            void it_returns_200_and_all_products() throws Exception {
                final ResultActions resultActions = subject();

                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].name").value(equalTo(givenProducts.get(0).getName())))
                        .andExpect(jsonPath("$[1].name").value(equalTo(givenProducts.get(1).getName())))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("상품이 존재하지 않으면")
        class Context_with_empty_db {
            @BeforeEach
            void prepare() {
                List<Product> products = productRepository.findAll();

                products.forEach(product -> productRepository.delete(product));
            }

            @Test
            @DisplayName("OK(200)와 빈 데이터를 리턴한다")
            void it_return_200_and_empty_array() throws Exception {
                final ResultActions resultActions = subject();

                resultActions.andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().string("[]"))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("detail[/products/id::GET] 메소드는")
    class Describe_detail {
        ResultActions subject(Long id) throws Exception {
            return mockMvc.perform(get("/products/{id}", id));
        }

        @Nested
        @DisplayName("유효한 ID가 주어지면")
        class Context_with_valid_id {
            private final Product givenProduct = productRepository.save(ProductSampleFactory.createProduct());
            private final Long PRODUCT_ID = givenProduct.getId();

            @Test
            @DisplayName("OK(200)와 요청한 상품을 리턴한다")
            void it_returns_200_and_searched_product() throws Exception {
                final ResultActions resultActions = subject(PRODUCT_ID);

                resultActions.andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("name").value(equalTo(givenProduct.getName())))
                        .andExpect(jsonPath("maker").value(equalTo(givenProduct.getMaker())))
                        .andDo(print());


            }
        }
        @Nested
        @DisplayName("유효하지않은 ID가 주어지면")
        class Context_with_invalid_id {
            private final Long INVALID_PRODUCT_ID = 9999L;

            @Test
            @DisplayName("NOT_FOUND(404)와 예외 메시지를 리턴한다")
            void it_returns_404_and_message() throws Exception {
                final ResultActions resultActions = subject(INVALID_PRODUCT_ID);

                resultActions.andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("message", containsString("Product not found")))
                        .andDo(print());

            }
        }
    }
}
