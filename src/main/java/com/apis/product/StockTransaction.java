package com.apis.product;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("stock_transactions")
public class StockTransaction {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    @Column("change_quantity")
    private Integer changeQuantity;

    @Column("transaction_type")
    private String transactionType;

    @Column("created_at")
    private LocalDateTime createdAt;
}