package fr.checkconsulting.scpiinvapi.batch.reports;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class CsvErrorCollector {
    private final List<CsvErrorReport> errors = new ArrayList<>();

    public void addError(CsvErrorReport error) {
        errors.add(error);
    }

    public List<CsvErrorReport> getErrors() {
        return new ArrayList<>(errors);
    }

    public void clear() {
        errors.clear();
    }
}
