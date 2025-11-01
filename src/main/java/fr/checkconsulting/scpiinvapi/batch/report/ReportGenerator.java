package fr.checkconsulting.scpiinvapi.batch.report;

import fr.checkconsulting.scpiinvapi.dto.request.BatchError;

import java.util.List;

public interface ReportGenerator {
    void generateReport(List<BatchError> errors, long totalLinesProcessed);
}
