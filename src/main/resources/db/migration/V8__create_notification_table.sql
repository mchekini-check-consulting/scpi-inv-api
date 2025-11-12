CREATE TABLE notification (
                              id BIGSERIAL PRIMARY KEY,
                              date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              recipient VARCHAR(50) NOT NULL,
                              type VARCHAR(20) NOT NULL,
                              investment_id BIGINT NOT NULL,
                              CONSTRAINT fk_notification_investment
                                  FOREIGN KEY (investment_id)
                                      REFERENCES investment(id)
                                      ON DELETE CASCADE
);