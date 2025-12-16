package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.FiscaliteDTOResponse;
import fr.checkconsulting.scpiinvapi.dto.response.RepartitionItemDto;
import fr.checkconsulting.scpiinvapi.model.entity.Profile;
import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import fr.checkconsulting.scpiinvapi.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
public class FiscaliteService {

    private final ProfileRepository profileRepository;
    private final UserService userService;

    public FiscaliteService(ProfileRepository profileRepository, UserService userService) {
        this.profileRepository = profileRepository;
        this.userService = userService;
    }


    public FiscaliteDTOResponse calculerFiscalite(BigDecimal revenuScpiBrut, List<RepartitionItemDto> locations) {

        String email = userService.getEmail();
        log.info("Début du calcul de fiscalité pour ivestor={} avec revenuScpiNet={} et locations={}" ,email, revenuScpiBrut, locations);

        Profile profilInvest = profileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profil introuvable pour l'email : " + email));

        BigDecimal revenuInvestisseur = profilInvest.getIncomeInvestor() != null ? profilInvest.getIncomeInvestor() : BigDecimal.ZERO;
        BigDecimal revenuConjoint = profilInvest.getIncomeConjoint() != null ? profilInvest.getIncomeConjoint() : BigDecimal.ZERO;
        BigDecimal revenuProfil = revenuInvestisseur.add(revenuConjoint);

        log.info("Revenu investisseur={} , revenu conjoint={} , revenuProfil={}", revenuInvestisseur, revenuConjoint, revenuProfil);

        BigDecimal nombreParts = calculerNombreParts(profilInvest.getStatus(), profilInvest.getChildren());
        log.info("Nombre de parts calculé={} pour maritalStatus={} et enfants={}", nombreParts, profilInvest.getStatus(), profilInvest.getChildren());

        BigDecimal deductionEnfants = calculerDeductionEnfants(profilInvest.getChildren());
        log.info("Déduction fiscale appliquée pour {} enfants = {}", profilInvest.getChildren(), deductionEnfants);

        BigDecimal quotient = revenuProfil.divide(nombreParts, 2, RoundingMode.HALF_UP);
        int oldTmi = determinerTMI(quotient);
        log.info("Quotient={} , ancien TMI={}", quotient, oldTmi);

        BigDecimal revenuGlobal = revenuProfil.add(revenuScpiBrut != null ? revenuScpiBrut : BigDecimal.ZERO);
        BigDecimal quotientGlobal = revenuGlobal.divide(nombreParts, 2, RoundingMode.HALF_UP);
        int newTmi = determinerTMI(quotientGlobal);

        BigDecimal impotParPart = calculerImpotsProgressifsSurQuotient(quotientGlobal);
        BigDecimal impotTotal = impotParPart.multiply(nombreParts).setScale(2, RoundingMode.HALF_UP);

        log.info("Revenu global={} , quotientGlobal={} , nouvel TMI={} , impotTotal={}", revenuGlobal, quotientGlobal, newTmi, impotTotal);

        BigDecimal prelevementsSociaux = BigDecimal.ZERO;
        if (revenuScpiBrut != null && locations != null) {
            for (RepartitionItemDto loc : locations) {
                log.info("localisation={} avec pourcentage={}", loc.getLabel(), loc.getPercentage());

                if ("France".equalsIgnoreCase(loc.getLabel())) {
                    BigDecimal partFrance = revenuScpiBrut.multiply(loc.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                    prelevementsSociaux = partFrance.multiply(new BigDecimal("0.172")).setScale(2, RoundingMode.HALF_UP);
                    log.info("Part française trouvée={} , prélèvements sociaux={}", partFrance, prelevementsSociaux);
                    break;
                }
            }
        }

        BigDecimal revenuNetApresFiscalite = revenuGlobal.subtract(impotTotal.add(prelevementsSociaux)).add(deductionEnfants);
        log.info("Revenu net après fiscalité et déduction enfants={}", revenuNetApresFiscalite);

        BigDecimal tauxMoyen = BigDecimal.ZERO;
        if (revenuGlobal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalFiscalite = impotTotal.add(prelevementsSociaux).subtract(deductionEnfants);
            tauxMoyen = totalFiscalite.divide(revenuGlobal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        log.info("Taux moyen fiscalité={}", tauxMoyen);

        boolean isFrench = locations.stream().anyMatch(loc -> "France".equalsIgnoreCase(loc.getLabel()));
        log.info("La SCPI contient une part en France ? {}", isFrench);

        log.info("Fin du calcul de fiscalité pour ivestor email={}", email);

        return FiscaliteDTOResponse.builder()
                .revenuProfil(revenuProfil)
                .revenuScpiBrut(revenuScpiBrut)
                .revenuGlobal(revenuGlobal)
                .oldTmi(oldTmi)
                .newTmi(newTmi)
                .tmiChanged(newTmi != oldTmi)
                .impotTotal(impotTotal)
                .prelevementsSociaux(prelevementsSociaux)
                .revenuNetApresFiscalite(revenuNetApresFiscalite)
                .tauxMoyen(tauxMoyen)
                .isFrench(isFrench)
                .build();
    }


    private BigDecimal calculerNombreParts(MaritalStatus maritalStatus, int nombreEnfants) {
        BigDecimal parts = switch (maritalStatus) {
            case CELIBATAIRE, DIVORCE, VEUF -> BigDecimal.ONE;
            case MARIE, PACSE -> new BigDecimal("2");
        };

        for (int i = 1; i <= nombreEnfants; i++) {
            parts = parts.add(i <= 2 ? new BigDecimal("0.5") : BigDecimal.ONE);
        }

        return parts;
    }

    private int determinerTMI(BigDecimal quotient) {
        if (quotient.compareTo(new BigDecimal("11497")) <= 0) return 0;
        if (quotient.compareTo(new BigDecimal("29315")) <= 0) return 11;
        if (quotient.compareTo(new BigDecimal("83823")) <= 0) return 30;
        if (quotient.compareTo(new BigDecimal("180294")) <= 0) return 41;
        return 45;
    }

    private BigDecimal calculerImpotsProgressifsSurQuotient(BigDecimal quotient) {

        BigDecimal impot = BigDecimal.ZERO;

        BigDecimal[] bornes = {
                new BigDecimal("0"),
                new BigDecimal("11497"),
                new BigDecimal("29315"),
                new BigDecimal("83823"),
                new BigDecimal("180294")
        };

        BigDecimal[] taux = {
                new BigDecimal("0"),
                new BigDecimal("0.11"),
                new BigDecimal("0.30"),
                new BigDecimal("0.41"),
                new BigDecimal("0.45")
        };

        for (int i = 0; i < bornes.length - 1; i++) {
            BigDecimal bas = bornes[i];
            BigDecimal haut = bornes[i + 1];

            if (quotient.compareTo(bas) > 0) {
                BigDecimal baseImposable = quotient.min(haut).subtract(bas);
                impot = impot.add(baseImposable.multiply(taux[i]).setScale(2, RoundingMode.HALF_UP));
            }
        }

        if (quotient.compareTo(bornes[4]) > 0) {
            BigDecimal baseImposable = quotient.subtract(bornes[4]);
            impot = impot.add(baseImposable.multiply(taux[4]).setScale(2, RoundingMode.HALF_UP));
        }

        return impot.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculerDeductionEnfants(int nombreEnfants) {
        BigDecimal demiPart = new BigDecimal("1751");
        int demiPartsTotal = 0;

        for (int i = 1; i <= nombreEnfants; i++) {
            demiPartsTotal += (i <= 2 ? 1 : 2);
        }

        return demiPart.multiply(BigDecimal.valueOf(demiPartsTotal));
    }

}

