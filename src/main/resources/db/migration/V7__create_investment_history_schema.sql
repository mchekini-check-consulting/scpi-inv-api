
ALTER TABLE history
    DROP CONSTRAINT IF EXISTS fk_history_investment;

ALTER TABLE history
    ALTER COLUMN investment_id TYPE BIGINT
    USING investment_id::BIGINT;

ALTER TABLE history
    ADD CONSTRAINT fk_history_investment
        FOREIGN KEY (investment_id)
        REFERENCES investment(id)
        ON DELETE CASCADE;

CREATE SEQUENCE IF NOT EXISTS history_id_seq;
ALTER TABLE history
    ALTER COLUMN id SET DEFAULT nextval('history_id_seq');

ALTER TABLE history
    ADD COLUMN IF NOT EXISTS creation_date TIMESTAMP;
