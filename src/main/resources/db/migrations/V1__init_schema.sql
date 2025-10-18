CREATE TABLE IF NOT EXISTS societes_gestion (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS scpis (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    minimum_souscription INTEGER,
    capitalisation BIGINT,
    frequence_loyers VARCHAR(50),
    frais_gestion NUMERIC(5,2),
    frais_souscription NUMERIC(5,2),
    delai_jouissance INTEGER,
    iban VARCHAR(50),
    bic VARCHAR(50),
    demembrement BOOLEAN,
    cashback INTEGER,
    versement_programme BOOLEAN,
    publicite TEXT,
    url_image VARCHAR(255),
    societe_gestion_id INTEGER,
    CONSTRAINT fk_scpis_societe_gestion
        FOREIGN KEY (societe_gestion_id)
        REFERENCES societes_gestion(id)
);

CREATE TABLE IF NOT EXISTS taux_distribution (
    id SERIAL PRIMARY KEY,
    scpi_id BIGINT NOT NULL,
    annee INT NOT NULL,
    taux_distribution DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_taux_distribution_scpi
        FOREIGN KEY (scpi_id) REFERENCES scpis(id),
    CONSTRAINT uq_taux_distribution UNIQUE (scpi_id, annee)
);

CREATE TABLE IF NOT EXISTS decote_demembrement (
    id SERIAL PRIMARY KEY,
    scpi_id BIGINT NOT NULL,
    duree_annee INT NOT NULL,
    pourcentage DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_decote_demembrement_scpi
        FOREIGN KEY (scpi_id) REFERENCES scpis(id),
    CONSTRAINT uq_decote_demembrement UNIQUE (scpi_id, duree_annee)
);

CREATE TABLE IF NOT EXISTS valeurs_scpi (
    id SERIAL PRIMARY KEY,
    scpi_id BIGINT NOT NULL,
    annee INT NOT NULL,
    prix_part DECIMAL(10,2) NOT NULL,
    valeur_reconstitution DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_valeurs_scpi_scpi
        FOREIGN KEY (scpi_id) REFERENCES scpis(id),
    CONSTRAINT uq_valeurs_scpi UNIQUE (scpi_id, annee)
);

CREATE TABLE IF NOT EXISTS localisations (
    id SERIAL PRIMARY KEY,
    pays VARCHAR(100),
    pourcentage NUMERIC(5,2),
    scpi_id INTEGER,
    CONSTRAINT fk_localisation_scpi
        FOREIGN KEY (scpi_id)
        REFERENCES scpis(id)
);

CREATE TABLE IF NOT EXISTS secteurs (
    id SERIAL PRIMARY KEY,
    secteur VARCHAR(100),
    pourcentage NUMERIC(5,2),
    scpi_id INTEGER,
    CONSTRAINT fk_secteurs_scpi
        FOREIGN KEY (scpi_id)
        REFERENCES scpis(id)
);

CREATE TABLE IF NOT EXISTS investisseurs (
    id SERIAL PRIMARY KEY,
    id_utilisateur VARCHAR(100),
    adresse VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS patrimoines_immobiliers (
    investisseur_id INTEGER NOT NULL,
    scpi_id INTEGER NOT NULL,
    PRIMARY KEY (investisseur_id, scpi_id),
    CONSTRAINT fk_patrimoine_investisseur
        FOREIGN KEY (investisseur_id)
        REFERENCES investisseurs(id),
    CONSTRAINT fk_patrimoine_scpi
        FOREIGN KEY (scpi_id)
        REFERENCES scpis(id)
);

CREATE TABLE IF NOT EXISTS documents (
    id SERIAL PRIMARY KEY,
    investisseur_id INT,
    nom VARCHAR(255),
    type VARCHAR(100),
    taille VARCHAR(50),
    date_upload TIMESTAMP DEFAULT NOW(),
    url VARCHAR(255),
    CONSTRAINT fk_documents_investisseur
        FOREIGN KEY (investisseur_id)
        REFERENCES investisseurs(id)
);
