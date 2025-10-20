package fr.checkconsulting.scpiinvapi.batch.mappers;

import fr.checkconsulting.scpiinvapi.dtos.requests.*;
import fr.checkconsulting.scpiinvapi.batch.exceptions.csvfille.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class ScpiFieldSetMapper implements FieldSetMapper<ScpiCSVDTORequest> {
    @Override
    public ScpiCSVDTORequest mapFieldSet(FieldSet fieldSet) throws BindException {

        try {
            Map<String, String> normalizedValues = normalizeFieldSet(fieldSet);

            ScpiCSVDTORequest scpi = new ScpiCSVDTORequest();
            scpi.setNom(normalizedValues.get("nom"));
            scpi.setMinimumSouscription(parseInt(normalizedValues.get("minimum_souscription")));
            scpi.setCapitalisation(parseBigDecimal(normalizedValues.get("capitalisation")));
            scpi.setFraisSouscription(parseBigDecimal(normalizedValues.get("frais_souscription")));
            scpi.setFraisGestion(parseBigDecimal(normalizedValues.get("frais_gestion")));
            scpi.setDelaiJouissance(parseInt(normalizedValues.get("delai_jouissance")));
            scpi.setFrequenceLoyers(normalizedValues.get("frequence_loyers"));
            scpi.setIban(normalizedValues.get("iban"));
            scpi.setBic(normalizedValues.get("bic"));
            scpi.setDemembrement(parseBoolean(normalizedValues.get("demembrement")));
            scpi.setCashback(parseInt(normalizedValues.get("cashback")));
            scpi.setVersementProgramme(parseBoolean(normalizedValues.get("versement_programme")));
            scpi.setPublicite(normalizedValues.get("publicite"));



            String locValues = normalizedValues.get("localisation");
            if (locValues != null && !locValues.isBlank()) {
                List<LocalisationDTORequest> locList = new java.util.ArrayList<>();
                String[] parts = locValues.split(",");
                for (int i = 0; i < parts.length; i += 2) {
                    String pays = parts[i].trim();
                    Double pourcentage = (i + 1 < parts.length) ? parseDouble(parts[i + 1]) : null;
                    locList.add(new LocalisationDTORequest(null, pays, pourcentage, null));
                }
                scpi.setLocalisations(locList);
            }


            String secteurValues = normalizedValues.get("secteurs");
            if (secteurValues != null && !secteurValues.isBlank()) {
                List<SecteurDTORequest> secteurList = new java.util.ArrayList<>();
                String[] parts = secteurValues.split(",");
                for (int i = 0; i < parts.length; i += 2) {
                    String secteur = parts[i].trim();
                    Double pourcentage = (i + 1 < parts.length) ? parseDouble(parts[i + 1]) : null;
                    secteurList.add(new SecteurDTORequest(null, secteur, pourcentage, null));
                }
                scpi.setSecteurs(secteurList);
            }


            String valeursScpiValues = normalizedValues.get("prix_part");
            String valeurReconstitutionValues = normalizedValues.get("valeur_reconstitution");

            String[] prixParts = valeursScpiValues.split(",");
            String[] valeursReconst = valeurReconstitutionValues.split(",");

            List<ValeursScpiDTORequest> valeursList = new ArrayList<>();
            for (int i = 0; i < prixParts.length; i++) {
                ValeursScpiDTORequest valeurs = new ValeursScpiDTORequest();
                valeurs.setPrixPart(parseBigDecimal(prixParts[i].trim()));
                valeurs.setValeurReconstitution(parseBigDecimal(valeursReconst[i].trim()));
                valeursList.add(valeurs);
            }
            scpi.setValeursScpi(valeursList);


            String tauxValues = normalizedValues.get("taux_distribution");
            if (tauxValues != null && !tauxValues.isBlank()) {
                List<TauxDistributionDTORequest> tauxList = new java.util.ArrayList<>();
                String[] parts = tauxValues.split(",");
                for (String t : parts) {
                    TauxDistributionDTORequest taux = new TauxDistributionDTORequest();
                    taux.setTauxDistribution(parseBigDecimal(t.trim()));
                    taux.setAnnee(LocalDate.now().getYear());
                    tauxList.add(taux);
                }
                scpi.setTauxDistributions(tauxList);
            }

            String decotesValues = normalizedValues.get("decote_demembrement");
            if (decotesValues != null && !decotesValues.isBlank()) {
                List<DecoteDemembrementDTORequest> decotesList = new java.util.ArrayList<>();
                String[] parts = decotesValues.split(",");

                for (int i = 0; i < parts.length - 1; i += 2) {
                    DecoteDemembrementDTORequest decote = new DecoteDemembrementDTORequest();
                    decote.setDureeAnnee(parseInt(parts[i].trim()));

                    java.math.BigDecimal bd = parseBigDecimal(parts[i + 1].trim());
                    decote.setPourcentage(bd != null ? bd.doubleValue() : null);

                    decotesList.add(decote);
                }

                scpi.setDecotesDemembrement(decotesList);
            }

            return scpi;

        } catch (Exception e) {
            log.error("Erreur lors du mappage CSV : {}", e.getMessage(), e);
            throw new CsvValidationException("Erreur de mappage CSV à la ligne suivante : " + e.getMessage(), e);
        }
    }

    private Map<String, String> normalizeFieldSet(FieldSet fieldSet) {
        Map<String, String> normalized = new HashMap<>();
        String[] names = fieldSet.getNames();
        log.debug("Colonnes détectées dans CSV : {}", Arrays.toString(names));

        for (String name : names) {
            String lower = name.trim().toLowerCase();
            String value = null;
            try {
                value = fieldSet.readString(name);
            } catch (Exception e) {
                log.warn("Impossible de lire la colonne '{}': {}", name, e.getMessage());
            }
            normalized.put(lower, value);
        }
        return normalized;
    }

    private Integer parseInt(String value) {
        try {
            return value != null && !value.isBlank() ? Integer.parseInt(value.trim()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            return value != null && !value.isBlank() ? Double.parseDouble(value.trim()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean parseBoolean(String value) {
        try {
            return value != null && !value.isBlank() && Boolean.parseBoolean(value.trim());
        } catch (Exception e) {
            return false;
        }
    }

    private java.math.BigDecimal parseBigDecimal(String value) {
        try {
            return value != null && !value.isBlank()
                    ? new java.math.BigDecimal(value.trim().replace(",", "."))
                    : null;
        } catch (Exception e) {
            return null;
        }

    }
}
