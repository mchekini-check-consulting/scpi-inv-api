CREATE TABLE IF NOT EXISTS scpi (

    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    minimum_subscription INTEGER,
    capitalization NUMERIC,
    rent_frequency VARCHAR(100),
    management_fees NUMERIC(5,2),
    subscription_fees NUMERIC(5,2),
    enjoyment_delay INTEGER,
    iban VARCHAR(50),
    bic VARCHAR(50),
    dismemberment BOOLEAN,
    cashback INTEGER,
    scheduled_payment BOOLEAN,
    advertising TEXT,
    image_url VARCHAR(255),
    manager VARCHAR(255)

);


CREATE TABLE IF NOT EXISTS distribution_rate (

    id BIGSERIAL PRIMARY KEY,
    scpi_id BIGINT NOT NULL,
    year INT NOT NULL,
    rate DECIMAL(10,2),

    CONSTRAINT fk_distribution_rate_scpi
    FOREIGN KEY (scpi_id) REFERENCES scpi(id),
    CONSTRAINT uq_distribution_rate UNIQUE (scpi_id, year)
    );

CREATE TABLE IF NOT EXISTS dismemberment_discounts (
    id BIGSERIAL PRIMARY KEY,
    scpi_id BIGINT NOT NULL,
    duration_years INT NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,

    CONSTRAINT fk_dismemberment_discount_scpi
    FOREIGN KEY (scpi_id) REFERENCES scpi(id),
    CONSTRAINT uq_dismemberment_discount UNIQUE (scpi_id, duration_years)
    );

CREATE TABLE IF NOT EXISTS scpi_part_values (

    id BIGSERIAL PRIMARY KEY,
    scpi_id BIGINT NOT NULL,
    year INT NOT NULL,
    share_price DECIMAL(10,2) NOT NULL,
    reconstitution_value DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_scpi_values_scpi
    FOREIGN KEY (scpi_id) REFERENCES scpi(id)
    );

CREATE TABLE IF NOT EXISTS location (
    id BIGSERIAL PRIMARY KEY,
    country VARCHAR(100),
    percentage NUMERIC(5,2),
    scpi_id BIGINT,

    CONSTRAINT fk_location_scpi
    FOREIGN KEY (scpi_id)
    REFERENCES scpi(id)
    );

CREATE TABLE IF NOT EXISTS sector (

    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    percentage NUMERIC(5,2),
    scpi_id BIGINT,

    CONSTRAINT fk_sectors_scpi
    FOREIGN KEY (scpi_id)
    REFERENCES scpi(id)
    );
