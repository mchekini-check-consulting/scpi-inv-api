package fr.checkconsulting.scpiinvapi.dto.response;

import fr.checkconsulting.scpiinvapi.model.enums.InvestmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryDto {

    LocalDateTime modificationDate;
    LocalDateTime creationDate;
    String status;
    Long investmentId;

    public HistoryDto(LocalDateTime modificationDate, LocalDateTime creationDate, InvestmentStatus status, Long investmentId) {
        this.modificationDate = modificationDate;
        this.creationDate = creationDate;
        this.investmentId = investmentId;
        this.status = switch (status) {
            case PENDING -> "PENDING";
            case SUCCESS -> "SUCCESS";
            case FAILED -> "FAILED";
        };
    }

}
