package fr.checkconsulting.scpiinvapi.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecteurDTORequest {
    private Long id;
    private String secteur;
    private Double pourcentage;
    private Long scpiId;
}
