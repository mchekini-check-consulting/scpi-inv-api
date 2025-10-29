package fr.checkconsulting.scpiinvapi.batch.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
public class BatchJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job scpiJob;

    public BatchJobScheduler(JobLauncher jobLauncher, Job scpiJob) {
        this.jobLauncher = jobLauncher;
        this.scpiJob = scpiJob;
    }
    @Scheduled(cron = "0 0 0 * * *")
    @PostConstruct
    public void runJob() {
        try {
            jobLauncher.run(scpiJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
            log.info("Job SCPI exécuté à {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Erreur lors de l'exécution du job SCPI", e);
        }
    }
}
