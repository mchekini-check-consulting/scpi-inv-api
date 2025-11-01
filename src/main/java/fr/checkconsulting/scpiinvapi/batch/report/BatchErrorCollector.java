package fr.checkconsulting.scpiinvapi.batch.report;

import fr.checkconsulting.scpiinvapi.dto.request.BatchError;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class BatchErrorCollector {
    private final List<BatchError> errors = new ArrayList<>();

    public void addError(int line, String type, String message) {

        errors.add(new BatchError(line, type, message));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void clear() {
        errors.clear();
    }
}
