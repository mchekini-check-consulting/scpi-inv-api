package fr.checkconsulting.scpiinvapi.batch.reportErrors;

import fr.checkconsulting.scpiinvapi.dto.request.BatchError;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerateErrorReportTasklet implements Tasklet {

    private final GenerateErrorReport errorReportService;
    private final BatchErrorCollector batchErrorCollector;

    public GenerateErrorReportTasklet(GenerateErrorReport errorReportService,
                                      BatchErrorCollector batchErrorCollector) {
        this.errorReportService = errorReportService;
        this.batchErrorCollector = batchErrorCollector;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        long totalLinesProcessed = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getStepExecutions()
                .stream()
                .mapToLong(StepExecution::getReadCount)
                .sum();

        List<BatchError> errors = batchErrorCollector.getErrors();

        if (!errors.isEmpty()) {
            errorReportService.generateAndUploadErrorReport(errors, totalLinesProcessed);
        }
        batchErrorCollector.clear();
        return RepeatStatus.FINISHED;
    }
}
