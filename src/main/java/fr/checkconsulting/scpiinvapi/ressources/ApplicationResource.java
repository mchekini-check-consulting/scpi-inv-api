package fr.checkconsulting.scpiinvapi.ressources;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class ApplicationResource {

    @Value("${app.name:DemoApplication}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.message:hello from backend}")
    private String appMessage;

    @GetMapping("info")
    public String getApplicationInfo() {
        return String.format(
                "Application: %s || Version: %s || Message: %s",
                appName, appVersion, appMessage
        );
    }
}
