
CREATE TABLE IF NOT EXISTS investor (
    user_id VARCHAR(100) PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    phone_number VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS investment (
    id BIGSERIAL PRIMARY KEY,
    investment_amount NUMERIC(15,2) NOT NULL,
    number_of_shares NUMERIC(15,2) NOT NULL,
    investment_type VARCHAR(20) NOT NULL,
    dismemberment_years INTEGER,
    investment_date TIMESTAMP NOT NULL DEFAULT NOW(),
    scpi_id BIGINT NOT NULL REFERENCES scpi(id),
    investor_id VARCHAR(100) NOT NULL REFERENCES investor(user_id)
);
