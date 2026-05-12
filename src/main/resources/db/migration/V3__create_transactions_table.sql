CREATE TABLE transactions (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount             DECIMAL(19, 2)  NOT NULL,
    type               VARCHAR(20)     NOT NULL,
    status             VARCHAR(20)     NOT NULL,
    sender_wallet_id   BIGINT,
    receiver_wallet_id BIGINT          NOT NULL,
    created_at         DATETIME        NOT NULL,
    CONSTRAINT fk_transaction_sender   FOREIGN KEY (sender_wallet_id)   REFERENCES wallets(id),
    CONSTRAINT fk_transaction_receiver FOREIGN KEY (receiver_wallet_id) REFERENCES wallets(id)
);