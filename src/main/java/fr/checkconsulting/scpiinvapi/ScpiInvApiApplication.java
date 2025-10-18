package fr.checkconsulting.scpiinvapi;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableBatchProcessing
@EnableJpaAuditing
public class ScpiInvApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScpiInvApiApplication.class, args);
    }

}
