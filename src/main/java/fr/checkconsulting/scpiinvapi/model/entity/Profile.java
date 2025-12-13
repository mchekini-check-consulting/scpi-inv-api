package fr.checkconsulting.scpiinvapi.model.entity;

import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
 
    private MaritalStatus status;
    private Integer children;
    private BigDecimal incomeInvestor;
    private BigDecimal incomeConjoint;
    private String email;


}