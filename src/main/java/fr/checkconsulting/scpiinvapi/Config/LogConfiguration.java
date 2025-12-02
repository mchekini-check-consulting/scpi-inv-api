package fr.checkconsulting.scpiinvapi.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogConfiguration {

    @Value("${spring.profiles.active}")
    private String environment;


    @Value("${spring.application.name}")
    private String applicationName;


    @PostConstruct
    public void configureLogContext() {

        MDC.put("env", environment);
        MDC.put("application", applicationName);

    }
}
