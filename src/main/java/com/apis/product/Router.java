package com.apis.product;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> create(ProductController productController) {
        return RouterFunctions.route(POST("/product"), productController::createProduct);
    }

    @Bean
    public RouterFunction<ServerResponse> getAllProducts(ProductController productController) {
        return RouterFunctions.route(GET("/product"), productController::getAllProducts);
    }

    @Bean
    public RouterFunction<ServerResponse> getProductById(ProductController productController) {
        return RouterFunctions.route(GET("/product/{id}"), productController::getProductById);
    }

    @Bean
    public RouterFunction<ServerResponse> updateTheProduct(ProductController productController) {
        return RouterFunctions.route(PUT("/product/{id}"), productController::updateTheProduct);
    }

    @Bean
    public RouterFunction<ServerResponse> deleteTheProduct(ProductController productController) {
        return RouterFunctions.route(DELETE("/product/{id}"), productController::deleteProductById);
    }

    @Bean
    public RouterFunction<ServerResponse> increaseStockRoute(ProductController productController) {
        return RouterFunctions.route(
                POST("/products/{id}/increase-stock"),
                productController::increaseStock
        );
    }

    @Bean
    public RouterFunction<ServerResponse> decreaseStockRoute(ProductController productController) {
        return RouterFunctions.route(
                POST("/products/{id}/decrease-stock"),
                productController::decreaseStock
        );
    }

    @Bean
    public RouterFunction<ServerResponse> lowStockRoute(ProductController productController) {
        return RouterFunctions.route(
                GET("/products/low-stock"),
                productController::getLowStockProducts
        );
    }

}
