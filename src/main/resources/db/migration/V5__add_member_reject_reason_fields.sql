ALTER TABLE members
    ADD COLUMN kyc_review_reason VARCHAR(500),
    ADD COLUMN rejected_by VARCHAR(120),
    ADD COLUMN rejected_at TIMESTAMP;
