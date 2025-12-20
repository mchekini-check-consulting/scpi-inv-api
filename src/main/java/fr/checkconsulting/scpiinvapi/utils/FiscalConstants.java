package fr.checkconsulting.scpiinvapi.utils;

import java.math.BigDecimal;
import java.util.List;

public class FiscalConstants {
    private FiscalConstants() {
    }

    public static final BigDecimal PRELEVEMENTS_SOCIAUX_FRANCE = new BigDecimal("0.172");

    public static final List<BigDecimal> BORNES_IR = List.of(
            new BigDecimal("0"),
            new BigDecimal("11497"),
            new BigDecimal("29315"),
            new BigDecimal("83823"),
            new BigDecimal("180294")
    );

    public static final List<BigDecimal> TAUX_IR = List.of(
            BigDecimal.ZERO,
            new BigDecimal("0.11"),
            new BigDecimal("0.30"),
            new BigDecimal("0.41"),
            new BigDecimal("0.45")
    );

    public static final BigDecimal VALEUR_DEMI_PART_ENFANT = new BigDecimal("1751");
}
