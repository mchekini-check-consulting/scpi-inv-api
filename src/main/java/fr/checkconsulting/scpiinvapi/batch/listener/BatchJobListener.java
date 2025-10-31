package fr.checkconsulting.scpiinvapi.batch.listener;

import fr.checkconsulting.scpiinvapi.batch.reportErrors.BatchErrorCollector;
import fr.checkconsulting.scpiinvapi.batch.reportErrors.GenerateErrorReport;
import fr.checkconsulting.scpiinvapi.dto.request.BatchError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchJobListener implements JobExecutionListener {

    private final GenerateErrorReport reportService;
    private final BatchErrorCollector errorCollector;

    @Getter
    private long totalLinesProcessed;

    @Getter
    private List<BatchError> errors;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("*********** Démarrage du job batch : {}", jobExecution.getJobInstance().getJobName());
        errorCollector.clear();
        totalLinesProcessed = 0;
        errors = List.of();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("*********** Fin du job batch : {}", jobExecution.getJobInstance().getJobName());

        totalLinesProcessed = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getReadCount)
                .sum();

        long totalInserted = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getWriteCount)
                .sum();

        long totalErrors = totalLinesProcessed - totalInserted;

        errors = errorCollector.getErrors();

        if (!errors.isEmpty()) {
            log.info("Total lines processed : {}", totalLinesProcessed);
            log.info("Successfully inserted : {}", totalInserted);
            log.info("Failed lines : {}", totalErrors);

            reportService.generateAndUploadErrorReport(errors, totalLinesProcessed);
            errorCollector.clear();
        } else {
            log.info("Aucune erreur détectée — aucun rapport généré.");
        }
    }
}
