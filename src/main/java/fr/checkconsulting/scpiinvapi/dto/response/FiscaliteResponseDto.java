package fr.checkconsulting.scpiinvapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = """
            Résultat du calcul de fiscalité SCPI pour un investisseur.
            
            Cette réponse fournit :
            - l’impact fiscal global du portefeuille SCPI
            - l’évolution de la tranche marginale d’imposition (TMI)
            - les prélèvements sociaux applicables
            - le revenu net après fiscalité
            """
)
public class FiscaliteResponseDto {
    @Schema(description = "Revenu annuel du profil avant intégration des revenus SCPI")
    private BigDecimal revenuProfil;

    @Schema(description = "Revenu annuel brut généré par les SCPI")
    private BigDecimal revenuScpiBrut;

    @Schema(description = "Revenu global après ajout des revenus SCPI (revenu profil + revenu brut SCPI)")
    private BigDecimal revenuGlobal;

    @Schema(description = "Tranche marginale d’imposition (TMI) avant investissement SCPI")
    private int oldTmi;

    @Schema(description = "Nouvelle tranche marginale d’imposition (TMI) après intégration des SCPI")
    private int newTmi;

    @Schema(description = "Indique si la tranche marginale d’imposition a changé suite à l’investissement")
    private boolean tmiChanged;

    @Schema(description = "Montant de l’impôt sur le revenu du profil AVANT intégration des SCPI")
    private BigDecimal impotProfilAvantScpi;

    @Schema(description = "Revenu net annuel du profil AVANT intégration des SCPI")
    private BigDecimal revenuNetAvantScpi;


    @Schema(description = "Montant total de l’impôt estimé (impôt sur le revenu lié aux SCPI)")
    private BigDecimal impotTotal;

    @Schema(description = """
                Montant total des prélèvements sociaux appliqués aux revenus SCPI.
                
                (17,2 % pour les revenus soumis en France )
                """)
    private BigDecimal prelevementsSociaux;
    @Schema(
            description = "Revenu net annuel après déduction de l’impôt et des prélèvements sociaux")

    private BigDecimal revenuNetApresFiscalite;
    @Schema(
            description = "Taux moyen d’imposition appliqué aux revenus SCPI")
    private BigDecimal tauxMoyen;

    @Schema(
            description = """
                Indique si la fiscalité appliquée correspond au régime français.
                
                - true : fiscalité France
                - false : fiscalité Europe
                """)
    private boolean isFrench;
}
