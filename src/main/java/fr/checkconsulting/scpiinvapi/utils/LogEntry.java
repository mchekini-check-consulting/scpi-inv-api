package fr.checkconsulting.scpiinvapi.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@Builder
public class LogEntry {

    private LocalDateTime timestamp;
    private String application;
    private String level;
    private String message;
    @Value("SPRING_PROFILES_ACTIVE")
    private String environment;
    private String thread;
    private String loggerClass;

    @Override
    public String toString() {
        return String.format("[%s] [%s] [%s] [%s] [%s] [%s] - %s",
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), application, environment, level, thread, loggerClass, message);
    }



}