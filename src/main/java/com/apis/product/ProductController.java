package com.apis.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;

    public Mono<ServerResponse> createProduct(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Product.class)
                .flatMap(product -> {
                    if (product.getStockQuantity() < 0) {
                        return ServerResponse.badRequest().bodyValue(Map.of("error", "stock cannot be smaller than 0"));
                    }
                    if (product.getLowStockThreshold() < 0) {
                        return ServerResponse.badRequest().bodyValue(Map.of("error", "Threshold cannot be smaller than 0"));
                    }
                    return productRepository.save(product)
                            .flatMap(product1 -> ServerResponse.ok().bodyValue(product1));
                }).switchIfEmpty(ServerResponse.badRequest().bodyValue(Map.of("error", "Request body is missing or Invalid body")));
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest serverRequest) {
        return productRepository.findAll().collectList().flatMap(products -> ServerResponse.ok().bodyValue(products));
    }

    public Mono<ServerResponse> getProductById(ServerRequest serverRequest) {
        Long id = Long.parseLong(serverRequest.pathVariable("id"));
        return productRepository.findById(id)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateTheProduct(ServerRequest serverRequest) {
        Long id = Long.parseLong(serverRequest.pathVariable("id"));
        return productRepository.findById(id)
                .flatMap(product -> serverRequest.bodyToMono(Product.class)
                        .flatMap(updatedProduct -> {
                            if (updatedProduct.getStockQuantity() < 0) {
                                return ServerResponse.badRequest().bodyValue(Map.of("error", "stock cannot be smaller than 0"));
                            }
                            if (product.getLowStockThreshold() < 0) {
                                return ServerResponse.badRequest().bodyValue(Map.of("error", "Threshold cannot be smaller than 0"));
                            }
                            return productRepository.save(updatedProduct).flatMap(product2 -> ServerResponse.ok().bodyValue(product2));
                        })).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteProductById(ServerRequest serverRequest) {
        Long id = Long.parseLong(serverRequest.pathVariable("id"));
        return productRepository.findById(id)
                .flatMap(product -> productRepository.deleteById(product.getId())
                        .then(ServerResponse.ok().bodyValue(Map.of("success", "Deleted successfully"))))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
