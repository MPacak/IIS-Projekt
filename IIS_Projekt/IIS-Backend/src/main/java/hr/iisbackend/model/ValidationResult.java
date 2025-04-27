package hr.iisbackend.model;

import lombok.Data;

import java.util.List;

@Data
public class ValidationResult {
    private final boolean valid;
    private final List<XMLValidationError> errors;

    public ValidationResult(boolean valid, List<XMLValidationError> errors) {
        this.valid = valid;
        this.errors = errors;
    }
}