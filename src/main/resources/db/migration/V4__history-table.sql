CREATE TABLE history (
                             id BIGINT PRIMARY KEY,
                             modification_date TIMESTAMP NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             investment_id INTEGER NOT NULL
);