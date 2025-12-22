package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.FiscaliteResponseDto;
import fr.checkconsulting.scpiinvapi.dto.response.RepartitionItemDto;
import fr.checkconsulting.scpiinvapi.model.entity.Profile;
import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import fr.checkconsulting.scpiinvapi.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static fr.checkconsulting.scpiinvapi.utils.FiscalConstants.*;

@Service
@Slf4j
public class FiscaliteService {

    private final ProfileRepository profileRepository;
    private final UserService userService;

    public FiscaliteService(ProfileRepository profileRepository, UserService userService) {
        this.profileRepository = profileRepository;
        this.userService = userService;
    }


    public FiscaliteResponseDto calculerFiscalite(BigDecimal revenuScpiBrut, List<RepartitionItemDto> locations) {

        String email = userService.getEmail();
        log.info("*****Début du calcul de fiscalité | ivestor={} avec revenuScpiNet={} et locations={}" ,email, revenuScpiBrut, locations);

        Profile profilInvest = profileRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Profil introuvable pour l'email={}", email);
                    return new RuntimeException("Profil introuvable pour l'email : " + email);
                });

        log.info("Profil fiscal => statut={} | enfants={}", profilInvest.getStatus(), profilInvest.getChildren());

        BigDecimal revenuInvestisseur = profilInvest.getIncomeInvestor() != null ? profilInvest.getIncomeInvestor() : BigDecimal.ZERO;
        BigDecimal revenuConjoint = profilInvest.getIncomeConjoint() != null ? profilInvest.getIncomeConjoint() : BigDecimal.ZERO;
        BigDecimal revenuProfil = revenuInvestisseur.add(revenuConjoint);

        log.info("Revenu investisseur={} , revenu conjoint={} , revenuProfil={}", revenuInvestisseur, revenuConjoint, revenuProfil);

        BigDecimal nombreParts = calculerNombreParts(profilInvest.getStatus(), profilInvest.getChildren());
        log.info("Nombre de parts calculé={} pour maritalStatus={} et enfants={}", nombreParts, profilInvest.getStatus(), profilInvest.getChildren());

        BigDecimal quotient = revenuProfil.divide(nombreParts, 2, RoundingMode.HALF_UP);
        int oldTmi = determinerTMI(quotient);
        log.info("Quotient AVANT SCPI => revenuProfil={} / parts={} => quotient={} | TMI={}",
                revenuProfil, nombreParts, quotient, oldTmi);

        log.info("Quotient={} , ancien TMI={}", quotient, oldTmi);

        BigDecimal impotProfilSansEnfants = calculerImpotSansEnfants(revenuProfil, profilInvest.getStatus());

        BigDecimal impotProfilAvecEnfants = calculerImpotAvecEnfants(revenuProfil, profilInvest.getStatus(), profilInvest.getChildren());

        BigDecimal avantageProfilEnfants = impotProfilSansEnfants.subtract(impotProfilAvecEnfants);

        if (avantageProfilEnfants.compareTo(BigDecimal.ZERO) < 0) {
            avantageProfilEnfants = BigDecimal.ZERO;
        }

        BigDecimal plafondProfilEnfants = calculerPlafondEnfants(profilInvest.getChildren());

        BigDecimal avantageProfilRetenu = avantageProfilEnfants.min(plafondProfilEnfants);

        BigDecimal impotProfilFinal = impotProfilSansEnfants.subtract(avantageProfilRetenu).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        BigDecimal revenuNetAvantScpi = revenuProfil.subtract(impotProfilFinal);

        log.info("REVENU PROFIL NET (avant SCPI) => revenuProfil={} | impotProfilFinal={} | revenuNetAvantScpi={}",
                revenuProfil,
                impotProfilFinal,
                revenuNetAvantScpi
        );

        log.info("IMPÔT PROFIL (avant SCPI) => impotSansEnfants={} | impotAvecEnfants={} | avantage={} | plafond={} | retenu={} | impotFinal={}",
                impotProfilSansEnfants,
                impotProfilAvecEnfants,
                avantageProfilEnfants,
                plafondProfilEnfants,
                avantageProfilRetenu,
                impotProfilFinal
        );

        BigDecimal revenuScpiImposable = revenuScpiBrut != null
                ? revenuScpiBrut
                : BigDecimal.ZERO;

        BigDecimal revenuGlobal = revenuProfil.add(revenuScpiImposable);

        BigDecimal quotientGlobal = revenuGlobal.divide(nombreParts, 2, RoundingMode.HALF_UP);
        int newTmi = determinerTMI(quotientGlobal);

        log.info("Quotient APRÈS SCPI => revenuGlobal={} / parts={} => quotientGlobal={} | TMI={}", revenuGlobal, nombreParts, quotientGlobal, newTmi);

        BigDecimal impotSansEnfants = calculerImpotSansEnfants(revenuGlobal, profilInvest.getStatus());

        BigDecimal impotAvecEnfants = calculerImpotAvecEnfants(revenuGlobal, profilInvest.getStatus(), profilInvest.getChildren());

        BigDecimal avantageEnfants = impotSansEnfants.subtract(impotAvecEnfants);
        if (avantageEnfants.compareTo(BigDecimal.ZERO) < 0) {
            avantageEnfants = BigDecimal.ZERO;
        }

        BigDecimal plafondAvantage = calculerPlafondEnfants(profilInvest.getChildren());
        BigDecimal avantageRetenu = avantageEnfants.min(plafondAvantage);

        BigDecimal impotTotal = impotSansEnfants.subtract(avantageRetenu)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("Impôt sans enfants={} | impôt avec enfants={} | avantage théorique={} | plafond={} | avantage retenu={} | impot final={}", impotSansEnfants, impotAvecEnfants, avantageEnfants, plafondAvantage, avantageRetenu, impotTotal);

        BigDecimal impotScpi = impotTotal.subtract(impotProfilFinal).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        log.info("Impôt sur le revenu généré par les SCPI = {}", impotScpi);

        log.info("Revenu global={} , quotientGlobal={} , nouvel TMI={} , impotTotal={}", revenuGlobal, quotientGlobal, newTmi, impotTotal);

        log.info(
                "Mécanisme enfants => impotSansEnfants={} | impotAvecEnfants={} | avantageTheorique={} | plafond={} | avantageRetenu={} | impotFinal={}",
                impotSansEnfants,
                impotAvecEnfants,
                avantageEnfants,
                plafondAvantage,
                avantageRetenu,
                impotTotal
        );

        BigDecimal prelevementsSociaux = BigDecimal.ZERO;

        if (revenuScpiBrut != null && locations != null) {
            for (RepartitionItemDto loc : locations) {
                log.info("localisation={} avec pourcentage={}", loc.getLabel(), loc.getPercentage());

                if ("France".equalsIgnoreCase(loc.getLabel())) {
                    BigDecimal partFrance = revenuScpiBrut.multiply(loc.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));

                    prelevementsSociaux = prelevementsSociaux.add(partFrance.multiply(PRELEVEMENTS_SOCIAUX_FRANCE));

                    log.info("Part française trouvée={} , prélèvements sociaux={}", partFrance, prelevementsSociaux);

                }
            }
        }
        else {
            log.info("Aucun revenu SCPI ou aucune localisation fournie");
        }
        prelevementsSociaux = prelevementsSociaux.setScale(2, RoundingMode.HALF_UP);
        log.info("Prélèvements sociaux totaux={}", prelevementsSociaux);

        BigDecimal revenuScpiNet = BigDecimal.ZERO;

        if (revenuScpiBrut != null) {
            revenuScpiNet = revenuScpiBrut
                    .subtract(impotScpi)
                    .subtract(prelevementsSociaux)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        log.info("Revenu SCPI annuel NET après fiscalité = {}", revenuScpiNet);

        BigDecimal revenuNetApresFiscalite = revenuGlobal.subtract(impotTotal.add(prelevementsSociaux));

        log.info("Revenu net après fiscalité et déduction enfants={}", revenuNetApresFiscalite);

        BigDecimal tauxMoyen = BigDecimal.ZERO;
        if (revenuGlobal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalFiscalite = impotTotal.add(prelevementsSociaux);

            tauxMoyen = totalFiscalite.divide(revenuGlobal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        log.info("Taux moyen fiscalité={}", tauxMoyen);

        log.info("Synthèse fiscalité => revenuProfil={} | revenuGlobal={} | impotTotal={} | prelevementsSociaux={} | revenuNet={} | tauxMoyen={}%",
                revenuProfil,
                revenuGlobal,
                impotTotal,
                prelevementsSociaux,
                revenuNetApresFiscalite,
                tauxMoyen
        );

        boolean isFrench = locations.stream().anyMatch(loc -> "France".equalsIgnoreCase(loc.getLabel()));
        log.info("La SCPI contient une part en France ? {}", isFrench);

        log.info("********Fin du calcul de fiscalité | ivestor email={}", email);

        return FiscaliteResponseDto.builder()
                .revenuProfil(revenuProfil)
                .revenuScpiBrut(revenuScpiBrut)
                .revenuGlobal(revenuGlobal)
                .oldTmi(oldTmi)
                .newTmi(newTmi)
                .tmiChanged(newTmi != oldTmi)
                .impotProfilAvantScpi(impotProfilFinal)
                .revenuNetAvantScpi(revenuNetAvantScpi)
                .impotTotal(impotTotal)
                .impotScpi(impotScpi)
                .revenuScpiNet(revenuScpiNet)
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
        if (quotient.compareTo(BORNES_IR.get(1)) <= 0) return 0;
        if (quotient.compareTo(BORNES_IR.get(2)) <= 0) return 11;
        if (quotient.compareTo(BORNES_IR.get(3)) <= 0) return 30;
        if (quotient.compareTo(BORNES_IR.get(4)) <= 0) return 41;
        return 45;
    }

    private BigDecimal calculerImpotsProgressifsSurQuotient(BigDecimal quotient) {

        BigDecimal impot = BigDecimal.ZERO;

        for (int i = 0; i < BORNES_IR.size()  - 1; i++) {
            BigDecimal bas = BORNES_IR.get(i);
            BigDecimal haut =  BORNES_IR.get(i + 1);

            if (quotient.compareTo(bas) > 0) {
                BigDecimal baseImposable = quotient.min(haut).subtract(bas);
                impot = impot.add(baseImposable.multiply(TAUX_IR.get(i)));
            }
        }

        int lastIndex = BORNES_IR.size() - 1;

        if (quotient.compareTo(BORNES_IR.get(lastIndex)) > 0) {
            BigDecimal baseImposable = quotient.subtract(BORNES_IR.get(lastIndex));
            impot = impot.add(baseImposable.multiply(TAUX_IR.get(lastIndex)));
        }

        return impot.setScale(2, RoundingMode.HALF_UP);
    }


    private BigDecimal calculerImpotSansEnfants(BigDecimal revenu, MaritalStatus status) {

        BigDecimal partsBase = (status == MaritalStatus.MARIE || status == MaritalStatus.PACSE)
                ? new BigDecimal("2")
                : BigDecimal.ONE;

        BigDecimal quotient = revenu.divide(partsBase, 2, RoundingMode.HALF_UP);

        return calculerImpotsProgressifsSurQuotient(quotient)
                .multiply(partsBase)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculerImpotAvecEnfants(BigDecimal revenu, MaritalStatus status, int enfants) {

        BigDecimal parts = calculerNombreParts(status, enfants);
        BigDecimal quotient = revenu.divide(parts, 2, RoundingMode.HALF_UP);

        return calculerImpotsProgressifsSurQuotient(quotient)
                .multiply(parts)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculerPlafondEnfants(int nombreEnfants) {

        int demiPartsTotal = 0;
        for (int i = 1; i <= nombreEnfants; i++) {
            demiPartsTotal += (i <= 2 ? 1 : 2);
        }

        return VALEUR_DEMI_PART_ENFANT
                .multiply(BigDecimal.valueOf(demiPartsTotal))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
}

