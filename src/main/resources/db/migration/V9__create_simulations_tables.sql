CREATE TABLE simulation (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    total_investment NUMERIC(18,2) DEFAULT 0,
    total_annual_return NUMERIC(18,2) DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE simulation_scpi (
    id BIGSERIAL PRIMARY KEY,
    simulation_id BIGINT NOT NULL,
    scpi_id BIGINT NOT NULL,
    shares INTEGER NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    annual_return NUMERIC(18,2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT fk_simulation FOREIGN KEY(simulation_id) REFERENCES simulation(id) ON DELETE CASCADE,
    CONSTRAINT fk_scpi FOREIGN KEY(scpi_id) REFERENCES scpi(id)
);

CREATE INDEX idx_simulation_user ON simulation(user_id);
