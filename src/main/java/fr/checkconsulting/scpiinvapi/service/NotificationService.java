package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.client.ScpiInvNotificationClient;
import fr.checkconsulting.scpiinvapi.dto.request.EmailNotificationRequest;
import fr.checkconsulting.scpiinvapi.model.entity.Investment;
import fr.checkconsulting.scpiinvapi.model.entity.Notification;
import fr.checkconsulting.scpiinvapi.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
@Slf4j
public class NotificationService {
    private final NotificationRepository repository;
    private final ScpiInvNotificationClient notificationClient;
    private final TemplateEngine templateEngine;

    public NotificationService(NotificationRepository repository,
                               ScpiInvNotificationClient notificationClient,
                               TemplateEngine templateEngine) {
        this.repository = repository;
        this.notificationClient = notificationClient;
        this.templateEngine = templateEngine;
    }

    public void sendEmailNotification(String recipient, Investment investment) {
        String emailContent = generateEmailContent(investment);

        EmailNotificationRequest emailRequest = EmailNotificationRequest.builder()
                .to(recipient)
                .subject("Confirmation de votre demande d'investissement")
                .body(emailContent)
                .bodyType("HTML")
                .from("me.chekini@gmail.com")
                .build();

        try {
            notificationClient.sendEmailNotification(emailRequest);
            repository.save(Notification.builder()
                    .recipient(recipient)
                    .date(LocalDateTime.now())
                    .type("EMAIL")
                    .investment(investment)
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email au service de notification", e);
        }
    }

    private String generateEmailContent(Investment investment) {
        Context context = new Context();

        String scpiName = investment.getScpi() != null ? investment.getScpi().getName() : "SCPI";
        String investmentType = mapInvestmentType(investment.getInvestmentType().name());

        context.setVariable("scpi_name", scpiName);
        context.setVariable("share_count", investment.getNumberOfShares());
        context.setVariable("amount", investment.getInvestmentAmount());
        context.setVariable("investment_type", investmentType);
        context.setVariable("dismemberment", investment.getDismembermentYears());

        return templateEngine.process("email/investment-confirmation", context);
    }

    private String mapInvestmentType(String investmentType) {
        return switch (investmentType.toUpperCase()) {
            case "FULL_OWNERSHIP" -> "Pleine propriété";
            case "BARE_OWNERSHIP" -> "Nue propriété";
            case "USUFRUCT" -> "Usufruit";
            default -> investmentType;
        };
    }
}
