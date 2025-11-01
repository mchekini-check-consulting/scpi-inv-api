package fr.checkconsulting.scpiinvapi.batch.report;

import fr.checkconsulting.scpiinvapi.dto.request.BatchError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GenerateErrorReport {
    private final ReportGenerator generateReport;

    public GenerateErrorReport( ReportGenerator generateReport) {
        this.generateReport = generateReport;
    }

    public void generateAndUploadErrorReport(List<BatchError> errors, long totalLinesProcessed) {
        generateReport.generateReport(errors,totalLinesProcessed);

    }

}