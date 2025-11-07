ALTER TABLE distribution_rate
    RENAME COLUMN "year" TO distribution_year;

ALTER TABLE distribution_rate
    DROP CONSTRAINT IF EXISTS uq_distribution_rate,
    ADD CONSTRAINT uq_distribution_rate UNIQUE (scpi_id, distribution_year);


ALTER TABLE scpi_part_values
    RENAME COLUMN "year" TO valuation_year;
