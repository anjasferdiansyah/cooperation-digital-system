ALTER TABLE members
    ADD COLUMN status VARCHAR(20),
    ADD COLUMN kyc_reviewed_at TIMESTAMP,
    ADD COLUMN kyc_reviewed_by VARCHAR(120);

UPDATE members
SET status = 'PENDING_KYC'
WHERE status IS NULL;

ALTER TABLE members
    ALTER COLUMN status SET NOT NULL;
