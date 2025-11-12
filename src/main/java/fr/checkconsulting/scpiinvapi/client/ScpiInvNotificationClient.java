package fr.checkconsulting.scpiinvapi.client;

import fr.checkconsulting.scpiinvapi.dto.request.EmailNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "scpi-inv-notification", url = "${notification-service-url}")
public interface ScpiInvNotificationClient {

    @PostMapping(value = "api/v1/email/send", consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<String> sendEmailNotification(@RequestBody EmailNotificationRequest emailRequest);
}
