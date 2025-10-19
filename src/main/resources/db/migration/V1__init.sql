CREATE schema if not exists scpi;
CREATE TABLE if not exists scpi.scpi
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    yield DOUBLE PRECISION NOT NULL
    );