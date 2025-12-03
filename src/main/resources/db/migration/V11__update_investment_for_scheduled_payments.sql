ALTER TABLE investment
    ADD COLUMN IF NOT EXISTS payment_type VARCHAR(20),

    ADD COLUMN IF NOT EXISTS scheduled_payment_date DATE,

    ADD COLUMN IF NOT EXISTS monthly_amount NUMERIC(15,2);
