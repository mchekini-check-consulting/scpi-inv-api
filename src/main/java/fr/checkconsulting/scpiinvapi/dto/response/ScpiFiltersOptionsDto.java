package fr.checkconsulting.scpiinvapi.dto.response;

import java.util.List;

public record ScpiFiltersOptionsDto(List<String> sectors, List<String> locations, List<String> rentFrequencies) {
}
