CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    children INT NOT NULL,
    income_investor NUMERIC(19,2) NOT NULL,
    income_conjoint NUMERIC(19,2)
);