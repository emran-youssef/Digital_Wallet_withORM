
CREATE TABLE wallets (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance    DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    created_at DATETIME       NOT NULL,
    updated_at DATETIME       NOT NULL,
    user_id    BIGINT         NOT NULL UNIQUE,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id)
);
