ALTER TABLE savings_accounts
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE savings_accounts
    ADD CONSTRAINT chk_savings_accounts_type
        CHECK (savings_type IN ('POKOK', 'WAJIB', 'SUKARELA')),
    ADD CONSTRAINT chk_savings_accounts_status
        CHECK (status IN ('ACTIVE', 'CLOSED')),
    ADD CONSTRAINT chk_savings_accounts_closed_after_opened
        CHECK (closed_at IS NULL OR closed_at >= opened_at),
    ADD CONSTRAINT uq_savings_accounts_id_member UNIQUE (id, member_id);

ALTER TABLE savings_transactions
    DROP CONSTRAINT fk_savings_transactions_account,
    DROP CONSTRAINT fk_savings_transactions_member;

ALTER TABLE savings_transactions
    ADD CONSTRAINT chk_savings_transactions_type
        CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'ADJUSTMENT')),
    ADD CONSTRAINT fk_savings_transactions_account_member
        FOREIGN KEY (savings_account_id, member_id) REFERENCES savings_accounts (id, member_id);
