package fr.checkconsulting.scpiinvapi.batch.processor;

import fr.checkconsulting.scpiinvapi.dtos.requests.LocalisationDTORequest;
import fr.checkconsulting.scpiinvapi.dtos.requests.ScpiCSVDTORequest;
import fr.checkconsulting.scpiinvapi.exceptions.csvfille.CsvValidationException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ScpiItemProcessor implements ItemProcessor<ScpiCSVDTORequest, ScpiCSVDTORequest> {
    @Override
    public ScpiCSVDTORequest process(ScpiCSVDTORequest item) throws Exception {

        int lineNumber = item.getLineNumber() != null ? item.getLineNumber() : -1;


        validateRequiredFields(item, lineNumber);

        validateLocalisationPercentage(item, lineNumber);


        ScpiCSVDTORequest scpi = new ScpiCSVDTORequest();
        scpi.setNom(item.getNom());
        scpi.setMinimumSouscription(item.getMinimumSouscription());
        scpi.setCapitalisation(item.getCapitalisation());
        scpi.setFraisSouscription(item.getFraisSouscription());
        scpi.setFraisGestion(item.getFraisGestion());
        scpi.setDelaiJouissance(item.getDelaiJouissance());
        scpi.setFrequenceLoyers(item.getFrequenceLoyers());
        scpi.setIban(item.getIban());
        scpi.setBic(item.getBic());
        scpi.setDemembrement(item.getDemembrement());
        scpi.setCashback(item.getCashback());
        scpi.setVersementProgramme(item.getVersementProgramme());
        scpi.setPublicite(item.getPublicite());


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
