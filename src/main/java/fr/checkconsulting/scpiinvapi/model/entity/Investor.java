package fr.checkconsulting.scpiinvapi.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Investor {

    @Id
    private String userId;
    private String userEmail;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;


    @OneToMany(mappedBy = "investor", cascade = CascadeType.ALL)
    private List<Investment> investments;

}
