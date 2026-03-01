ALTER TABLE members
    ADD COLUMN phone_number VARCHAR(20);

WITH numbered_members AS (
    SELECT id, '628100000' || LPAD(ROW_NUMBER() OVER (ORDER BY id)::text, 4, '0') AS generated_phone_number
    FROM members
    WHERE phone_number IS NULL
)
UPDATE members m
SET phone_number = numbered_members.generated_phone_number
FROM numbered_members
WHERE m.id = numbered_members.id;

ALTER TABLE members
    ALTER COLUMN phone_number SET NOT NULL;

ALTER TABLE members
    ADD CONSTRAINT uq_members_phone_number UNIQUE (phone_number);
