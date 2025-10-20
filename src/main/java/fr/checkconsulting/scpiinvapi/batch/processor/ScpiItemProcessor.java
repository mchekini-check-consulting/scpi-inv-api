package fr.checkconsulting.scpiinvapi.batch.processor;

import fr.checkconsulting.scpiinvapi.batch.reports.CsvErrorCollector;
import fr.checkconsulting.scpiinvapi.batch.reports.CsvErrorReport;
import fr.checkconsulting.scpiinvapi.dtos.requests.LocalisationDTORequest;
import fr.checkconsulting.scpiinvapi.dtos.requests.ScpiCSVDTORequest;
import fr.checkconsulting.scpiinvapi.batch.exceptions.csvfille.CsvValidationException;
import fr.checkconsulting.scpiinvapi.models.entities.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScpiItemProcessor implements ItemProcessor<ScpiCSVDTORequest, Scpi> {
    private final CsvErrorCollector csverrorCollector;


    @Override
    public Scpi process(ScpiCSVDTORequest scpiCsvDto) throws Exception {

        int lineNumber = scpiCsvDto.getLineNumber() != null ? scpiCsvDto.getLineNumber() : -1;


        try {

            validateRequiredFields(scpiCsvDto, lineNumber);
            validateLocalisationPercentage(scpiCsvDto, lineNumber);

        } catch (CsvValidationException e) {
            log.error("Erreur à la ligne {} (colonne '{}') : {}", e.getLineNumber(), e.getColumnName(), e.getMessage());
            csverrorCollector.addError(new CsvErrorReport(e.getLineNumber(), e.getColumnName(), e.getMessage()));
            return null;
        }


        validateLocalisationPercentage(scpiCsvDto, lineNumber);

        Scpi scpi = Scpi.builder()
                .nom(scpiCsvDto.getNom())
                .minimumSouscription(scpiCsvDto.getMinimumSouscription())
                .capitalisation(scpiCsvDto.getCapitalisation() != null ? scpiCsvDto.getCapitalisation().longValue() : null)
                .fraisSouscription(scpiCsvDto.getFraisSouscription())
                .fraisGestion(scpiCsvDto.getFraisGestion())
                .delaiJouissance(scpiCsvDto.getDelaiJouissance())
                .frequenceLoyers(scpiCsvDto.getFrequenceLoyers())
                .iban(scpiCsvDto.getIban())
                .bic(scpiCsvDto.getBic())
                .demembrement(scpiCsvDto.getDemembrement())
                .cashback(scpiCsvDto.getCashback())
                .versementProgramme(scpiCsvDto.getVersementProgramme())
                .publicite(scpiCsvDto.getPublicite())
                .localisations(new ArrayList<>())
                .secteurs(new ArrayList<>())
                .tauxDistributions(new ArrayList<>())
                .valeursScpi(new ArrayList<>())
                .build();

        ValeursScpi valeurs = ValeursScpi.builder()
                .prixPart(scpiCsvDto.getPrixPart())
                .valeurReconstitution(scpiCsvDto.getValeurReconstitution())
                .scpi(scpi)
                .build();
        scpi.getValeursScpi().add(valeurs);

        if (scpiCsvDto.getLocalisations() != null) {
            scpiCsvDto.getLocalisations().forEach(locDto -> {
                Localisation loc = Localisation.builder()
                        .pays(locDto.getPays())
                        .pourcentage(locDto.getPourcentage() != null
                                ? BigDecimal.valueOf(locDto.getPourcentage())
                                : null)
                        .scpi(scpi)
                        .build();
                scpi.getLocalisations().add(loc);
            });
        }

        if (scpiCsvDto.getSecteurs() != null) {
            scpiCsvDto.getSecteurs().forEach(secDto -> {
                Secteur sec = Secteur.builder()
                        .secteur(secDto.getSecteur())
                        .pourcentage(secDto.getPourcentage() != null
                                ? BigDecimal.valueOf(secDto.getPourcentage())
                                : null)
                        .scpi(scpi)
                        .build();
                scpi.getSecteurs().add(sec);
            });
        }

        if (scpiCsvDto.getTauxDistributions() != null) {
            scpiCsvDto.getTauxDistributions().forEach(tdDto -> {
                TauxDistribution td = TauxDistribution.builder()
                        .tauxDistribution(tdDto.getTauxDistribution())
                        .annee(tdDto.getAnnee())
                        .scpi(scpi)
                        .build();
                scpi.getTauxDistributions().add(td);
            });
        }

        if (scpiCsvDto.getDecotesDemembrement() != null) {
            scpiCsvDto.getDecotesDemembrement().forEach(decoteDto -> {
                DecoteDemembrement decote = DecoteDemembrement.builder()
                        .dureeAnnee(decoteDto.getDureeAnnee())
                        .pourcentage(decoteDto.getPourcentage() != null
                                ? BigDecimal.valueOf(decoteDto.getPourcentage())
                                : null)
                        .scpi(scpi)
                        .build();
                scpi.getDecotesDemembrement().add(decote);
            });
        }


        return scpi;
    }

    private void validateRequiredFields(ScpiCSVDTORequest item, int lineNumber) {
        if (!StringUtils.hasText(item.getNom()))
            throw new CsvValidationException("Le nom est obligatoire", "nom", lineNumber);

        if (item.getMinimumSouscription() == null)
            throw new CsvValidationException("Le minimum de souscription est obligatoire", "minimum_souscription", lineNumber);

        if (item.getCapitalisation() == null)
            throw new CsvValidationException("La capitalisation est obligatoire", "capitalisation", lineNumber);

        if (item.getFraisSouscription() == null)
            throw new CsvValidationException("Les frais de souscription sont obligatoires", "frais_souscription", lineNumber);

        if (item.getFraisGestion() == null)
            throw new CsvValidationException("Les frais de gestion sont obligatoires", "frais_gestion", lineNumber);

        if (!StringUtils.hasText(item.getIban()))
            throw new CsvValidationException("L’IBAN est obligatoire", "iban", lineNumber);

        if (!StringUtils.hasText(item.getBic()))
            throw new CsvValidationException("Le BIC est obligatoire", "bic", lineNumber);
    }
// il me rste encore dautres

    private void validateLocalisationPercentage(ScpiCSVDTORequest item, int lineNumber) {
        if (item.getLocalisations() == null || item.getLocalisations().isEmpty()) {
            throw new CsvValidationException("Au moins une localisation est requise", "localisation", lineNumber);
        }

        double total = item.getLocalisations()
                .stream()
                .mapToDouble(LocalisationDTORequest::getPourcentage)
                .sum();

        if (Math.abs(total - 100.0) > 0.01) {
            throw new CsvValidationException(
                    "Le total des pourcentages de localisation doit être égal à 100% (actuellement " + total + "%)",
                    "localisation",
                    lineNumber
            );
        }
    }

}
