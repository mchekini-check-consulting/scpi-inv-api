package fr.checkconsulting.scpiinvapi;

import fr.checkconsulting.scpiinvapi.config.TestcontainersConfig;
import org.springframework.boot.SpringApplication;

public class ScpiInvApiTestApplication {
    public static void main(String[] args) {
        SpringApplication.from(ScpiInvApiApplication::main)
                .with(TestcontainersConfig.class)
                .run(args);
    }
}