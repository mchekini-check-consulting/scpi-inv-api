ALTER TABLE investment
ADD COLUMN number_of_shares_month NUMERIC(10,2);

ALTER TABLE investment
ALTER COLUMN number_of_shares DROP NOT NULL;

ALTER TABLE investment
ALTER COLUMN investment_amount DROP NOT NULL;
