package fr.checkconsulting.scpiinvapi.batch.listener;

import fr.checkconsulting.scpiinvapi.batch.reporterrors.BatchErrorCollector;
import fr.checkconsulting.scpiinvapi.batch.reporterrors.GenerateErrorReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchJobListener implements JobExecutionListener {
    private final GenerateErrorReport reportService;
    private final BatchErrorCollector errorCollector;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("***********Démarrage du job batch : {}", jobExecution.getJobInstance().getJobName());
        errorCollector.clear();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("***********Fin du job batch : {}", jobExecution.getJobInstance().getJobName());

        long totalLinesProcessed = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getReadCount)
                .sum();

        long totalInserted = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getWriteCount)
                .sum();

        long totalErrors = totalLinesProcessed - totalInserted;

        if (errorCollector.hasErrors()) {
            log.info("Total lines processed : {}", totalLinesProcessed);
            log.info("Successfully inserted : {}", totalInserted);
            log.info("Failed lines : {}", totalErrors);

            reportService.generateErrorReport(errorCollector.getErrors(), totalLinesProcessed);
            errorCollector.clear();
        } else {
            log.info("Aucune erreur détectée — aucun rapport généré.");
        }
    }

}
