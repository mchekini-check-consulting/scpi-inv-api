package fr.checkconsulting.scpiinvapi.batch.config;

import fr.checkconsulting.scpiinvapi.batch.processor.ScpiItemProcessor;

import fr.checkconsulting.scpiinvapi.batch.reader.ScpiItemReader;
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

    private final ScpiItemReader scpiItemReader;
    private final ScpiItemProcessor processor;
    private final ScpiItemWriter writer;

    public BatchConfig(ScpiItemReader scpiItemReader, ScpiItemProcessor processor, ScpiItemWriter writer) {
        this.scpiItemReader = scpiItemReader;
        this.processor = processor;
        this.writer = writer;
    }

    @Bean
    public Step importScpiStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager
                               ) {
        return new StepBuilder("importScpiStep", jobRepository)
                .<ScpiDto, Scpi>chunk(10, transactionManager)
                .reader(scpiItemReader.reader())
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .noSkip(FlatFileParseException.class)
                .retry(TransientDataAccessException.class)
                .retry(SocketTimeoutException.class)
                .retry(HttpServerErrorException.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    public Job importScpiJob(JobRepository jobRepository, Step importScpiStep) {
        return new JobBuilder("importScpiJob", jobRepository)
                .start(importScpiStep)
                .build();
    }
}
