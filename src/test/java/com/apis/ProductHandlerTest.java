package com.apis;

import com.apis.product.Product;
import com.apis.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ProductHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setup() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("A sample product")
                .stockQuantity(50)
                .lowStockThreshold(5)
                .build();
    }

    @Test
    void testCreateProductSuccess() {
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(testProduct));

        webTestClient.post()
                .uri("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testProduct)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Product")
                .jsonPath("$.stockQuantity").isEqualTo(50);
    }

    @Test
    void testCreateProductWithNegativeStock() {
        Product invalidProduct = testProduct.toBuilder().stockQuantity(-5).build();

        webTestClient.post()
                .uri("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidProduct)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("stock cannot be smaller than 0");
    }

    @Test
    void testCreateProductWithNegativeThreshold() {
        Product invalidProduct = testProduct.toBuilder().lowStockThreshold(-1).build();

        webTestClient.post()
                .uri("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidProduct)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Threshold cannot be smaller than 0");
    }

    @Test
    void testCreateProductWithEmptyBody() {
        webTestClient.post()
                .uri("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Request body is missing or Invalid body");
    }

    @Test
    void testToIncreaseTheStock() {
        when(productRepository.findById(1L)).thenReturn(Mono.just(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(testProduct.toBuilder().stockQuantity(60).build()));

        webTestClient.post()
                .uri("/products/{id}/increase-stock?quantity=10", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stockQuantity").isEqualTo(60);
    }

    @Test
    void testToDecreaseTheStockSuccessfully() {
        int decreaseQuantity = 20;
        int expectedStock = testProduct.getStockQuantity() - decreaseQuantity;

        when(productRepository.findById(1L)).thenReturn(Mono.just(testProduct));
        when(productRepository.save(any(Product.class)))
                .thenReturn(Mono.just(testProduct.toBuilder().stockQuantity(expectedStock).build()));

        webTestClient.post()
                .uri("/products/{id}/decrease-stock?quantity={qty}", 1L, decreaseQuantity)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stockQuantity").isEqualTo(40);
    }


    @Test
    void testDecreaseStockBeyondAvailable() {
        when(productRepository.findById(1L)).thenReturn(Mono.just(testProduct));

        webTestClient.post()
                .uri("/products/{id}/decrease-stock?quantity=100", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Insufficient stock available");
    }

    @Test
    void testIncreaseStockWithNegativeQuantity() {
        webTestClient.post()
                .uri("/products/{id}/increase-stock?quantity=-10", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Quantity must be greater than 0");
    }

    @Test
    void testDecreaseStockWithNegativeQuantity() {
        webTestClient.post()
                .uri("/products/{id}/decrease-stock?quantity=-5", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Quantity must be greater than 0");
    }

    @Test
    void testModifyStockForNonExistentProduct() {
        when(productRepository.findById(999L)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/products/{id}/increase-stock?quantity=10", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody();
    }
}
