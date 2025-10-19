package fr.checkconsulting.scpiinvapi.batch.constants;

import java.util.List;

public final class ScpiImportConstants {
    private ScpiImportConstants() { ////j'empêche l'instanciation
    }

    public static final String CSV_SCPI_PATH = "data/catalogue_scpis.csv";

    public static final List<String> EXPECTED_HEADERS = List.of(
            "Nom", "Taux_distribution", "Minimum_souscription",
            "Localisation", "Secteurs", "Prix_part", "Capitalisation", "Gerant",
            "Frais_souscription", "Frais_gestion", "Delai_jouissance",
            "Frequence_loyers", "Valeur_reconstitution", "Iban", "Bic",
            "Decote_demembrement", "Demembrement", "Cashback",
            "Versement_programme", "Publicite"
    );

    public static final List<String> EXPECTED_HEADERS_LOWER = EXPECTED_HEADERS.stream()
            .map(String::toLowerCase)
            .toList();
}
