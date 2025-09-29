package com.apis.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("products")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private Long id;

    private String name;

    private String description;

    @Column("stock_quantity")
    private Integer stockQuantity;

    @Column("low_stock_threshold")
    @Builder.Default
    private Integer lowStockThreshold = 0;
}