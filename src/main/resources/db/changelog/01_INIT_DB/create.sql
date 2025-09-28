CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    low_stock_threshold INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS stock_transactions (
     id BIGSERIAL PRIMARY KEY,
     product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
     change_quantity INT NOT NULL,
     transaction_type VARCHAR(10) NOT NULL CHECK (transaction_type IN ('INCREASE','DECREASE')),
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);