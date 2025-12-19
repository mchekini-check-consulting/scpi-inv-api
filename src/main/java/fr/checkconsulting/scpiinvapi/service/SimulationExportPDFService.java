package fr.checkconsulting.scpiinvapi.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import fr.checkconsulting.scpiinvapi.dto.response.FiscaliteResponseDto;
import fr.checkconsulting.scpiinvapi.dto.response.RepartitionItemDto;
import fr.checkconsulting.scpiinvapi.dto.response.SimulationExportPDFDto;
import fr.checkconsulting.scpiinvapi.model.entity.Simulation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimulationExportPDFService {

    private final SimulationService simulationService;
    private final FiscaliteService fiscaliteService;
    private final TemplateEngine templateEngine;

    public byte[] exportSimulationPdf(Long simulationId) {

        log.info("Début export PDF | simulationId={}", simulationId);

        Simulation simulation = simulationService.getSimulationEntityById(simulationId);

        List<RepartitionItemDto> repartition = buildRepartitionGeoAgregee(simulation);

        log.info("Répartition géographique utilisée pour PDF => {}", repartition);

        FiscaliteResponseDto fiscalite = fiscaliteService.calculerFiscalite(simulation.getTotalAnnualReturn(), repartition);

        SimulationExportPDFDto export = buildExportDto(simulation, fiscalite);

        Context context = new Context();
        context.setVariable("export", export);

        String html = templateEngine.process("simulation/simulation-export", context);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();

            log.info("PDF généré avec succès | simulationId={}", simulationId);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Erreur génération PDF | simulationId={}", simulationId, e);
            throw new IllegalStateException("Erreur génération PDF", e);
        }
    }

    private List<RepartitionItemDto> buildRepartitionGeoAgregee(Simulation simulation) {

        BigDecimal totalAnnualReturn = simulation.getTotalAnnualReturn();

        if (totalAnnualReturn == null || totalAnnualReturn.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        Map<String, BigDecimal> repartitionMap = new HashMap<>();

        simulation.getItems().forEach(item ->
                item.getScpi().getLocations().forEach(loc -> {

                    BigDecimal part = item.getAnnualReturn()
                                    .multiply(loc.getPercentage())
                                    .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);

                    repartitionMap.merge(loc.getCountry(), part, BigDecimal::add
                    );
                })
        );

        return repartitionMap.entrySet().stream()
                .map(entry -> RepartitionItemDto.builder()
                        .label(entry.getKey())
                        .percentage(entry.getValue()
                                        .multiply(BigDecimal.valueOf(100))
                                        .divide(totalAnnualReturn, 8, RoundingMode.HALF_UP))
                        .build()
                )
                .toList();
    }


    private SimulationExportPDFDto buildExportDto(
            Simulation simulation,
            FiscaliteResponseDto fiscalite
    ) {

        List<String> scpiNames = new ArrayList<>();
        List<Integer> scpiShares = new ArrayList<>();
        List<BigDecimal> scpiInvestedAmounts = new ArrayList<>();
        List<BigDecimal> scpiAnnualReturns = new ArrayList<>();
        List<BigDecimal> scpiDistributionRates = new ArrayList<>();
        List<String> scpiLocalisations = new ArrayList<>();

        simulation.getItems().forEach(item -> {

            scpiNames.add(item.getScpi().getName());
            scpiShares.add(item.getShares());
            scpiInvestedAmounts.add(item.getAmount());
            scpiAnnualReturns.add(item.getAnnualReturn());

            BigDecimal rate = item.getScpi().getDistributionRates().isEmpty()
                            ? BigDecimal.ZERO
                            : item.getScpi().getDistributionRates().get(0).getRate();

            scpiDistributionRates.add(rate);

            String localisation = item.getScpi().getLocations().stream()
                            .map(l -> l.getCountry() + " " + l.getPercentage() + "%")
                            .collect(Collectors.joining(" / "));

            scpiLocalisations.add(localisation);
        });

        BigDecimal revenuAvantScpi = Optional.ofNullable(fiscalite.getRevenuNetAvantScpi())
                        .orElse(BigDecimal.ZERO);

        BigDecimal revenuApresScpi = Optional.ofNullable(fiscalite.getRevenuNetApresFiscalite())
                        .orElse(BigDecimal.ZERO);

        BigDecimal gainNet = revenuApresScpi.subtract(revenuAvantScpi);

        BigDecimal netYield = BigDecimal.ZERO;
        if (simulation.getTotalInvestment() != null
                && simulation.getTotalInvestment().compareTo(BigDecimal.ZERO) > 0) {

            netYield = gainNet.divide(simulation.getTotalInvestment(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        log.info("PDF – Synthèse Fiscale | revenuAvant={} | revenuApres={} | gainNet={} | rendementNet={}",
                revenuAvantScpi,
                revenuApresScpi,
                gainNet,
                netYield);

        return SimulationExportPDFDto.builder()
                .simulationId(simulation.getId())
                .simulationName(simulation.getName())
                .generatedAt(LocalDateTime.now())

                .totalInvestment(simulation.getTotalInvestment())
                .totalAnnualReturn(simulation.getTotalAnnualReturn())

                .netRevenueAfterTax(revenuApresScpi)
                .netYieldPercentage(netYield)

                .revenuAvantScpi(revenuAvantScpi)
                .revenuApresScpi(revenuApresScpi)
                .gainNet(gainNet)

                .fiscalite(fiscalite)

                .scpiNames(scpiNames)
                .scpiShares(scpiShares)
                .scpiInvestedAmounts(scpiInvestedAmounts)
                .scpiAnnualReturns(scpiAnnualReturns)
                .scpiDistributionRates(scpiDistributionRates)
                .scpiLocalisations(scpiLocalisations)

                .build();
    }

}
