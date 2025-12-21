package fr.checkconsulting.scpiinvapi.dto.request;

import java.util.List;

public record ScpiSearchCriteriaDto(String name, String type, Integer minimumSubscription, Double yield,
                                    List<String> countries, List<String> sectors, List<String> rentFrequencies) {

}
