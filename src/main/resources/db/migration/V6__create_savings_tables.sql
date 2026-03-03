CREATE TABLE savings_accounts (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL,
    account_no VARCHAR(40) NOT NULL,
    savings_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    opened_at TIMESTAMP NOT NULL,
    closed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT uq_savings_accounts_account_no UNIQUE (account_no),
    CONSTRAINT uq_savings_accounts_member_type UNIQUE (member_id, savings_type),
    CONSTRAINT fk_savings_accounts_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT chk_savings_accounts_balance_non_negative CHECK (balance >= 0)
);

CREATE INDEX idx_savings_accounts_member_id ON savings_accounts (member_id);
CREATE INDEX idx_savings_accounts_opened_at ON savings_accounts (opened_at);

CREATE TABLE savings_transactions (
    id UUID PRIMARY KEY,
    savings_account_id UUID NOT NULL,
    member_id UUID NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    reference_no VARCHAR(60) NOT NULL,
    note VARCHAR(255),
    occurred_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT uq_savings_transactions_reference_no UNIQUE (reference_no),
    CONSTRAINT fk_savings_transactions_account FOREIGN KEY (savings_account_id) REFERENCES savings_accounts (id),
    CONSTRAINT fk_savings_transactions_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT chk_savings_transactions_amount_positive CHECK (amount > 0)
);

CREATE INDEX idx_savings_transactions_account_id ON savings_transactions (savings_account_id);
CREATE INDEX idx_savings_transactions_member_id ON savings_transactions (member_id);
CREATE INDEX idx_savings_transactions_occurred_at ON savings_transactions (occurred_at);
