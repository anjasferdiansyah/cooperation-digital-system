ALTER TABLE members
    ADD COLUMN address VARCHAR(255),
    ADD COLUMN nik VARCHAR(16);

UPDATE members
SET address = 'UNKNOWN'
WHERE address IS NULL;

WITH numbered_members AS (
    SELECT id, LPAD(ROW_NUMBER() OVER (ORDER BY id)::text, 16, '0') AS generated_nik
    FROM members
    WHERE nik IS NULL
)
UPDATE members m
SET nik = numbered_members.generated_nik
FROM numbered_members
WHERE m.id = numbered_members.id;

ALTER TABLE members
    ALTER COLUMN address SET NOT NULL,
    ALTER COLUMN nik SET NOT NULL;

ALTER TABLE members
    ADD CONSTRAINT uq_members_nik UNIQUE (nik);
