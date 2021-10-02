package com.codesoom.assignment.domain;

import com.codesoom.assignment.convertors.ViewSupplier;
import com.codesoom.assignment.dto.ProductData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.codesoom.assignment.ConstantsForProductTest.두더지잡기;
import static com.codesoom.assignment.ConstantsForProductTest.스크래쳐;
import static com.codesoom.assignment.ConstantsForProductTest.츄르;
import static com.codesoom.assignment.ConstantsForProductTest.펫모닝;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("ProductList 일급 컬렉션 테스트")
class ProductListTest {
    private List<Product> products;

    @BeforeEach
    void setUp() {
        products = Arrays.asList(츄르, 펫모닝, 두더지잡기, 스크래쳐);
    }


    @DisplayName("newInstance 정적 팩토리 메서드로 새로운 일급 컬렉션을 만들 수 있다.")
    @Test
    void newInstance() {
        final ProductList productList1 = ProductList.newInstance();
        final ProductList productList2 = ProductList.newInstance();

        assertThat(productList1.isEmpty()).isTrue();
        assertThat(productList2.isEmpty()).isTrue();
        assertThat(productList1).isNotEqualTo(productList2);
    }

    @DisplayName("from 정적 팩토리 메서드로 상품 목록을 보관하는 일급 컬렉션을 만들 수 있다.")
    @Test
    void from() {
        final ProductList productList = ProductList.from(products);

        assertThat(productList.size()).isEqualTo(products.size());

        for (int i = 0; i < productList.size(); i++) {
            final Product product = productList.get(i);
            assertThat(products).contains(product);
        }
    }

    @DisplayName("Iterable 을 구현하는 상품 목록을 저장할 수 있다.")
    @Test
    void addAll() {
        final List<Product> 츄르_펫모닝 = Arrays.asList(츄르, 펫모닝);
        final Set<Product> 스크래쳐_두더지잡기 = new HashSet<>(Arrays.asList(스크래쳐, 두더지잡기));

        final ProductList productList = ProductList.newInstance();
        productList.addAll(츄르_펫모닝);
        productList.addAll(스크래쳐_두더지잡기);

        assertThat(productList.size()).isEqualTo(츄르_펫모닝.size() + 스크래쳐_두더지잡기.size());
    }

    @DisplayName("상품을 추가할 수 있다.")
    @Test
    void add() {
        final ProductList productList = ProductList.newInstance();
        int size = 0;

        for (Product product : products) {
            productList.add(product);
            size++;
            assertThat(productList.size()).isEqualTo(size);
        }

        for (int i = 0; i < productList.size(); i++) {
            final Product product = productList.get(i);
            assertThat(products).contains(product);
        }
    }

    @DisplayName("모든 객체를 DTO로 변환하여 반환할 수 있다.")
    @ParameterizedTest
    @MethodSource("provideConvertSupplier")
    void convert(ViewSupplier<Product, ?> supplier, Class<?> type) {
        final ProductList productList = ProductList.from(products);

        final List<?> convertedList = productList.map(supplier);

        for (Object obj : convertedList) {
            assertThat(obj).isInstanceOf(type);
        }

    }

    public static Stream<Arguments> provideConvertSupplier() {
        ViewSupplier<Product, ProductData> productViewDataSupplier = ProductData::from;

        return Stream.of(
                Arguments.of(productViewDataSupplier, ProductData.class)
        );
    }

    @DisplayName("모든 상품 목록을 반환할 수 있으며 반환된 값을 수정해도 내부의 값은 바뀌지 않는다.")
    @Test
    void getAll() {
        final ProductList productList = ProductList.from(products);
        final int productListSize = productList.size();

        final List<Product> getAllProduct = productList.getAll();
        getAllProduct.remove(0);

        assertThat(getAllProduct.size()).isEqualTo(productListSize - 1);
        assertThat(productList.size()).isEqualTo(productListSize);

    }

    @DisplayName("유효한 인덱스로 상품을 조회할 수 있다.")
    @Test
    void get() {
        final ProductList productList = ProductList.from(products);

        for (int i = 0; i < productList.size(); i++) {
            final Product foundProduct = productList.get(i);

            assertThat(foundProduct).isNotNull();
            assertThat(products).contains(foundProduct);
        }
    }

    @DisplayName("상품정보로 상품을 삭제할 수 있다.")
    @Test
    void removeWithProduct() {
        final ProductList productList = ProductList.from(products);

        for (int i = 0; i < products.size(); i++) {
            final Product product = products.get(i);

            productList.remove(product);
        }

        assertThat(productList.isEmpty()).isTrue();
    }

    @DisplayName("인덱스 정보로 상품을 삭제할 수 있다.")
    @Test
    void removeWithIndex() {
        final ProductList productList = ProductList.from(products);
        final int productSize = productList.size();

        for (int i = 0; i < productSize; i++) {
            productList.remove(0);
        }

        assertThat(productList.isEmpty()).isTrue();
    }

    @DisplayName("존재하지 않는 상품을 삭제요청해도 에러가 발생하지 않는다.")
    @Test
    void removeWithNotExistsProduct() {
        final ProductList productList = ProductList.from(products);

        final Product notExistsProduct = Product.builder()
                .name("테스트")
                .build();

        assertThatCode(() -> productList.remove(notExistsProduct))
                .doesNotThrowAnyException();
    }

    @Test
    void isEmpty() {
        final ProductList productList = ProductList.newInstance();

        assertThat(productList.isEmpty()).isTrue();

        productList.addAll(products);

        assertThat(productList.isEmpty()).isFalse();

    }

    @Test
    void size() {
        final ProductList productList = ProductList.newInstance();

        for (int i = 0, size = 1; i < products.size(); i++, size++) {
            productList.add(products.get(i));
            assertThat(productList.size()).isEqualTo(size);
        }

    }
}
