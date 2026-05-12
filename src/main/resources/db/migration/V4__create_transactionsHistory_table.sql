CREATE TABLE transaction_history (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount         DECIMAL(19, 2) NOT NULL,
    type           VARCHAR(20)    NOT NULL,
    status         VARCHAR(20)    NOT NULL,
    wallet_id      BIGINT         NOT NULL,
    transaction_id BIGINT         NOT NULL UNIQUE,
    archived_at    DATETIME       NOT NULL,
    CONSTRAINT fk_history_wallet      FOREIGN KEY (wallet_id)      REFERENCES wallets(id),
    CONSTRAINT fk_history_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);