package com.apis.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockTransactionRepository extends ReactiveCrudRepository<StockTransaction, Long> {
}
