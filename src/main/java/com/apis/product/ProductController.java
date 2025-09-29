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
    private final StockTransactionRepository stockTransactionRepository;

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
                            Product productToSave = Product.builder()
                                    .id(id)
                                    .name(updatedProduct.getName())
                                    .description(updatedProduct.getDescription())
                                    .stockQuantity(updatedProduct.getStockQuantity())
                                    .lowStockThreshold(updatedProduct.getLowStockThreshold())
                                    .build();

                            return productRepository.save(productToSave).flatMap(product2 -> ServerResponse.ok().bodyValue(product2));
                        }).switchIfEmpty(ServerResponse.badRequest().bodyValue(Map.of("error", "Request body is missing or Invalid body"))))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteProductById(ServerRequest serverRequest) {
        Long id = Long.parseLong(serverRequest.pathVariable("id"));
        return productRepository.findById(id)
                .flatMap(product -> productRepository.deleteById(product.getId())
                        .then(ServerResponse.ok().bodyValue(Map.of("success", "Deleted successfully"))))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> increaseStock(ServerRequest serverRequest) {
        Long id = Long.parseLong(serverRequest.pathVariable("id"));
        int quantity = Integer.parseInt(serverRequest.queryParam("quantity").orElse("0"));

        if (quantity <= 0) {
            return ServerResponse.badRequest()
                    .bodyValue(Map.of("error", "Quantity must be greater than 0"));
        }

        return productRepository.findById(id)
                .flatMap(product -> {
                    product.setStockQuantity(product.getStockQuantity() + quantity);
                    StockTransaction tx = new StockTransaction();
                    tx.setProductId(product.getId());
                    tx.setChangeQuantity(quantity);
                    tx.setTransactionType("INCREASE");

                    return stockTransactionRepository.save(tx)
                            .then(productRepository.save(product))
                            .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> decreaseStock(ServerRequest serverRequest) {
        Long id = Long.parseLong(serverRequest.pathVariable("id"));
        int quantity = Integer.parseInt(serverRequest.queryParam("quantity").orElse("0"));

        if (quantity <= 0) {
            return ServerResponse.badRequest()
                    .bodyValue(Map.of("error", "Quantity must be greater than 0"));
        }

        return productRepository.findById(id)
                .flatMap(product -> {
                    if (product.getStockQuantity() < quantity) {
                        return ServerResponse.badRequest()
                                .bodyValue(Map.of("error", "Insufficient stock available"));
                    }

                    product.setStockQuantity(product.getStockQuantity() - quantity);
                    StockTransaction tx = new StockTransaction();
                    tx.setProductId(product.getId());
                    tx.setChangeQuantity(quantity);
                    tx.setTransactionType("DECREASE");

                    return stockTransactionRepository.save(tx)
                            .then(productRepository.save(product))
                            .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getLowStockProducts(ServerRequest serverRequest) {
        return productRepository.findAll()
                .filter(product -> product.getStockQuantity() < product.getLowStockThreshold())
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products));
    }
}
