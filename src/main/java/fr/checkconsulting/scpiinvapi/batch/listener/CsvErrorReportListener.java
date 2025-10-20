package fr.checkconsulting.scpiinvapi.batch.listener;

import fr.checkconsulting.scpiinvapi.batch.reports.CsvErrorCollector;
import fr.checkconsulting.scpiinvapi.batch.reports.CsvErrorReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CsvErrorReportListener implements StepExecutionListener {

    private final CsvErrorCollector errorCollector;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        errorCollector.clear();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        List<CsvErrorReport> errors = errorCollector.getErrors();
        if (!errors.isEmpty()) {
            errors.forEach(e ->
                    log.error("Ligne {}, Colonne '{}', Erreur: {}", e.getLineNumber(), e.getColumnName(), e.getMessage())
            );
        }
        return null;
    }
}
