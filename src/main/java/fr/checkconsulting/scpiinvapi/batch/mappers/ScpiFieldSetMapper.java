package fr.checkconsulting.scpiinvapi.batch.mappers;

import fr.checkconsulting.scpiinvapi.dtos.requests.*;
import fr.checkconsulting.scpiinvapi.exceptions.csvfille.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class ScpiFieldSetMapper implements FieldSetMapper<ScpiCSVDTORequest> {
    @Override
    public ScpiCSVDTORequest mapFieldSet(FieldSet fieldSet) throws BindException {
        ScpiCSVDTORequest scpi = new ScpiCSVDTORequest();
        try {
            scpi.setNom(fieldSet.readString("nom"));
            scpi.setMinimumSouscription(fieldSet.readInt("minimum_souscription"));
            scpi.setCapitalisation(fieldSet.readBigDecimal("capitalisation"));
            scpi.setFraisSouscription(fieldSet.readBigDecimal("frais_souscription"));
            scpi.setFraisGestion(fieldSet.readBigDecimal("frais_gestion"));
            scpi.setDelaiJouissance(fieldSet.readInt("delai_jouissance"));
            scpi.setFrequenceLoyers(fieldSet.readString("frequence_loyers"));
            scpi.setIban(fieldSet.readString("iban"));
            scpi.setBic(fieldSet.readString("bic"));
            scpi.setDemembrement(fieldSet.readBoolean("demembrement"));
            scpi.setCashback(fieldSet.readInt("cashback"));
            scpi.setVersementProgramme(fieldSet.readBoolean("versement_programme"));
            scpi.setPublicite(fieldSet.readString("publicite"));

            scpi.setLocalisations(List.of(
                    new LocalisationDTORequest(null, fieldSet.readString("localisation"),
                            fieldSet.readDouble("pourcentage_localisation"), null)
            ));

            scpi.setSecteurs(List.of(
                    new SecteurDTORequest(null, fieldSet.readString("secteur"),
                            fieldSet.readDouble("pourcentage_secteur"), null)
            ));

            ValeursScpiDTORequest valeurs = new ValeursScpiDTORequest();
            valeurs.setPrixPart(fieldSet.readBigDecimal("prix_part"));
            valeurs.setValeurReconstitution(fieldSet.readBigDecimal("valeur_reconstitution"));


            TauxDistributionDTORequest taux = new TauxDistributionDTORequest();
            taux.setTauxDistribution(fieldSet.readBigDecimal("taux_distribution"));
            taux.setAnnee(LocalDate.now().getYear());
            scpi.setTauxDistributions(List.of(taux));

            return scpi;

        } catch (Exception e) {
            log.error("Erreur lors du mappage CSV : {}", e.getMessage(), e);
            throw new CsvValidationException("Erreur de mappage CSV à la ligne courante : " + e.getMessage(), e);
        }
    }
}
