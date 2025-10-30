package fr.checkconsulting.scpiinvapi.batch.reporterrors;

import fr.checkconsulting.scpiinvapi.dto.request.BatchError;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class GenerateErrorReport {
    public void generateErrorReport(List<BatchError> errors, long totalLinesProcessed) {

        int failedCount = errors.size();
        long successCount = totalLinesProcessed - failedCount;

        if (errors.isEmpty()) {
            log.info("No errors to include in the PDF report.");
            return;
        }

        try {
            File reportDir = new File("src/main/resources/reports");
            if (!reportDir.exists()) reportDir.mkdirs();

            String fileName = String.format("src/main/resources/reports/scpi_error_report_%s.pdf",
                    LocalDateTime.now().toString().replace(":", "-"));

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("SCPI Import Error Report");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 730);
                contentStream.showText("Report date: " + LocalDateTime.now());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 710);
                contentStream.showText("Total lines processed: " + totalLinesProcessed);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Successfully inserted: " + successCount);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Failed lines: " + failedCount);
                contentStream.endText();

                int yPosition = 660;
                contentStream.setFont(PDType1Font.HELVETICA, 10);

                for (BatchError error : errors) {
                    if (yPosition < 50) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = 750;
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                    }

                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, yPosition);
                    contentStream.showText(String.format("[%s] Line %d - %s",
                            error.getType(), error.getLineNumber(), error.getMessage()));
                    contentStream.endText();
                    yPosition -= 15;
                }

                contentStream.close();
                document.save(fileName);

                log.info("Error report generated: {}", fileName);
                System.out.println("Report generated at: " + new File(fileName).getAbsolutePath());

            } catch (IOException e) {
                log.error("Error while generating PDF report", e);
            }

        } catch (Exception e) {
            log.error("Unexpected error during PDF report generation", e);
        }
    }
}
