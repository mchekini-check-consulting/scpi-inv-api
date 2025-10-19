package fr.checkconsulting.scpiinvapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScpiDto {
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @JsonProperty("name")
    private String name;

    @DecimalMin(value = "0.0", message = "Le rendement doit être positif")
    @JsonProperty("yield")
    private Double yield;
}
