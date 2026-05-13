

ALTER TABLE transaction_history
DROP FOREIGN KEY fk_history_transaction;

-- drops the unique constraints of transaction_id in transaction_history table
ALTER TABLE transaction_history
DROP INDEX transaction_id;

ALTER TABLE transaction_history
ADD CONSTRAINT fk_history_transaction
FOREIGN KEY (transaction_id) REFERENCES transactions(id);