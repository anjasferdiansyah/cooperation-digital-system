ALTER TABLE savings_transactions
    ADD COLUMN external_reference VARCHAR(60);

CREATE INDEX idx_savings_transactions_external_reference
    ON savings_transactions (external_reference);
