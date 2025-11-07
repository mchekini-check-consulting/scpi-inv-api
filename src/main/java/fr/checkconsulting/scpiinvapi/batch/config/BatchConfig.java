package fr.checkconsulting.scpiinvapi.batch.config;

import fr.checkconsulting.scpiinvapi.exception.MissingColumnException;
import fr.checkconsulting.scpiinvapi.batch.listener.BatchJobListener;
import fr.checkconsulting.scpiinvapi.batch.processor.ScpiItemProcessor;
import fr.checkconsulting.scpiinvapi.batch.report.BatchErrorCollector;
import fr.checkconsulting.scpiinvapi.batch.report.GenerateErrorReport;
import fr.checkconsulting.scpiinvapi.batch.report.GenerateErrorReportTasklet;
import fr.checkconsulting.scpiinvapi.batch.writer.ScpiItemWriter;
import fr.checkconsulting.scpiinvapi.dto.request.ScpiDto;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpServerErrorException;

import java.net.SocketTimeoutException;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {

    private final FlatFileItemReader<ScpiDto> scpiReader;
    private final ScpiItemProcessor processor;
    private final ScpiItemWriter writer;
    private final BatchJobListener batchJobListener;
    private final GenerateErrorReport errorReportService;
    private final BatchErrorCollector batchErrorCollector;

    public BatchConfig(FlatFileItemReader<ScpiDto> scpiReader, ScpiItemProcessor processor, ScpiItemWriter writer,
                       BatchJobListener batchJobListener, GenerateErrorReport errorReportService,
                       BatchErrorCollector batchErrorCollector)
    {
        this.scpiReader = scpiReader;
        this.processor = processor;
        this.writer = writer;
        this.batchJobListener = batchJobListener;
        this.errorReportService = errorReportService;
        this.batchErrorCollector = batchErrorCollector;
    }

    @Bean
    public Step importScpiStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("importScpiStep", jobRepository)
                .<ScpiDto, Scpi>chunk(10, transactionManager)
                .reader(scpiReader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .noSkip(MissingColumnException.class)
                .noSkip(FlatFileParseException.class)
                .retry(TransientDataAccessException.class)
                .retry(SocketTimeoutException.class)
                .retry(HttpServerErrorException.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    public Step generateErrorReportStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager) {
        return new StepBuilder("generateErrorReportStep", jobRepository)
                .tasklet(new GenerateErrorReportTasklet(errorReportService, batchErrorCollector), transactionManager)
                .build();
    }

    @Bean
    public Job importScpiJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Step importStep = importScpiStep(jobRepository, transactionManager);
        Step errorStep = generateErrorReportStep(jobRepository, transactionManager);

        return new JobBuilder("importScpiJob", jobRepository)
                .listener(batchJobListener)
                .start(importStep)
                .on("FAILED").to(errorStep)
                .from(importStep)
                .on("COMPLETED").end()
                .end()
                .build();
    }
}
