package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.response.RepartitionItemDto;
import fr.checkconsulting.scpiinvapi.dto.response.SimulationResponseDto;
import fr.checkconsulting.scpiinvapi.dto.response.SimulationScpiResponseDto;
import fr.checkconsulting.scpiinvapi.model.entity.Simulation;
import fr.checkconsulting.scpiinvapi.model.entity.SimulationScpi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SimulationMapper {
    @Mapping(target = "items", source = "items")
    @Mapping(target = "locations", expression = "java(mapLocations(simulation.getItems()))")
    @Mapping(target = "sectors", expression = "java(mapSectors(simulation.getItems()))")
    SimulationResponseDto toDto(Simulation simulation);


    @Mapping(target = "scpiId", source = "scpi.id")
    @Mapping(target = "scpiName", source = "scpi.name")
    @Mapping(target = "locations", expression = "java(mapItemLocations(simulationScpi))")
    @Mapping(target = "sectors", expression = "java(mapItemSectors(simulationScpi))")
    SimulationScpiResponseDto toItemDto(SimulationScpi simulationScpi);

    List<SimulationResponseDto> toDtoList(List<Simulation> simulations);

    default List<RepartitionItemDto> mapLocations(List<SimulationScpi> items) {
        if (items == null || items.isEmpty()) return List.of();

        return items.stream()
                .flatMap(item -> item.getScpi().getLocations().stream()
                        .map(loc -> new RepartitionItemDto(
                                loc.getCountry(),
                                loc.getPercentage(),
                                null
                        )))
                .collect(Collectors.toList());
    }

    default List<RepartitionItemDto> mapSectors(List<SimulationScpi> items) {
        if (items == null || items.isEmpty()) return List.of();

        return items.stream()
                .flatMap(item -> item.getScpi().getSectors().stream()
                        .map(sec -> new RepartitionItemDto(
                                sec.getName(),
                                sec.getPercentage(),
                                null
                        )))
                .collect(Collectors.toList());
    }

    default List<RepartitionItemDto> mapItemLocations(SimulationScpi item) {
        if (item.getScpi().getLocations() == null) return List.of();

        return item.getScpi().getLocations().stream()
                .map(loc -> new RepartitionItemDto(
                        loc.getCountry(),
                        loc.getPercentage(),
                        null
                ))
                .collect(Collectors.toList());
    }

    default List<RepartitionItemDto> mapItemSectors(SimulationScpi item) {
        if (item.getScpi().getSectors() == null) return List.of();

        return item.getScpi().getSectors().stream()
                .map(sec -> new RepartitionItemDto(
                        sec.getName(),
                        sec.getPercentage(),
                        null
                ))
                .collect(Collectors.toList());
    }

}
