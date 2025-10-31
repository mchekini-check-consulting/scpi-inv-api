package fr.checkconsulting.scpiinvapi.batch.reportErrors;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import fr.checkconsulting.scpiinvapi.dto.request.BatchError;
import fr.checkconsulting.scpiinvapi.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GenerateErrorReport {

    private final SpringTemplateEngine templateEngine;
    private final MinioService minioService;

    public GenerateErrorReport(SpringTemplateEngine templateEngine, MinioService minioService) {
        this.templateEngine = templateEngine;
        this.minioService = minioService;
    }

    public void generateAndUploadErrorReport(List<BatchError> errors, long totalLinesProcessed) {
        log.info("=== Starting PDF error report generation ===");
        log.info("Total lines processed: {}", totalLinesProcessed);

        if (errors == null || errors.isEmpty()) {
            log.info("No errors found. Skipping PDF generation.");
            return;
        }

        int failedCount = errors.size();
        long successCount = totalLinesProcessed - failedCount;
        double failureRate = totalLinesProcessed == 0 ? 0.0 : (failedCount * 100.0 / totalLinesProcessed);

        try {
            List<BatchError> formattedErrors = errors.stream()
                    .map(this::formatErrorMessage)
                    .collect(Collectors.toList());

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

            minioService.uploadFile(pdfBytes, "data", fileName, "application/pdf");

            log.info("PDF error report uploaded successfully to MinIO bucket 'data' as '{}'", fileName);

        } catch (Exception e) {
            log.error("Unexpected error during PDF report generation and upload", e);
        } finally {
            log.info("=== PDF error report generation finished ===");
        }
    }

    private BatchError formatErrorMessage(BatchError error) {
        if (error.getMessage() == null) {
            return error;
        }

        String formattedMessage = error.getMessage()
                .replaceAll("-{6,}", "")
                .replaceAll("\\s+", " ")
                .replaceAll("\\(\\s+", "(")
                .replaceAll("\\s+\\)", ")")
                .replaceAll("tolérance\\s*\\[", "tolérance [")
                .replaceAll("\\]\\s*,", "], ")
                .trim();

        BatchError formattedError = new BatchError();
        formattedError.setType(error.getType());
        formattedError.setLineNumber(error.getLineNumber());
        formattedError.setMessage(formattedMessage);

        return formattedError;
    }
}
