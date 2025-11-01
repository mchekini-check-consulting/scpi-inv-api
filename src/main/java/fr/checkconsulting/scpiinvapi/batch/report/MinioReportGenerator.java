package fr.checkconsulting.scpiinvapi.batch.report;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import fr.checkconsulting.scpiinvapi.dto.request.BatchError;
import fr.checkconsulting.scpiinvapi.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Profile({"int", "qua"})
@Slf4j
public class MinioReportGenerator implements ReportGenerator {

    @Value("${spring.profiles.active}")
    private String env;

    private final SpringTemplateEngine templateEngine;
    private final MinioService minioService;

    public MinioReportGenerator(SpringTemplateEngine templateEngine, MinioService minioService) {
        this.templateEngine = templateEngine;
        this.minioService = minioService;
    }

    @Override
    public void generateReport(List<BatchError> errors, long totalLinesProcessed) {
        if (errors == null || errors.isEmpty()) return;

        int failedCount = errors.size();
        long successCount = totalLinesProcessed - failedCount;
        double failureRate = totalLinesProcessed == 0 ? 0.0 : (failedCount * 100.0 / totalLinesProcessed);

        try {
            List<BatchError> formattedErrors = new ArrayList<>(errors);

            Context ctx = new Context(Locale.FRENCH);
            ctx.setVariable("reportDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            ctx.setVariable("totalLines", totalLinesProcessed);
            ctx.setVariable("successCount", successCount);
            ctx.setVariable("failedCount", failedCount);
            ctx.setVariable("failureRate", String.format("%.2f", failureRate));
            ctx.setVariable("errors", formattedErrors);

            String htmlContent = templateEngine.process("error-report", ctx);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();

            byte[] pdfBytes = outputStream.toByteArray();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "scpi_error_report_" + timestamp + ".pdf";

            String bucketName = env + "-data";
            log.info("uploading file {} to bucket {}", fileName, bucketName);
            minioService.uploadFile(pdfBytes, bucketName, fileName, "application/pdf");

        } catch (Exception e) {
            log.error("Unexpected error during MinIO PDF report generation", e);
        }
    }


}
