package hr.iisbackend.model;

import lombok.Data;

@Data
public class XMLValidationError {
    private final String severity;
    private final int line;
    private final int column;
    private final String message;

    public XMLValidationError(String severity, int line, int column, String message) {
        this.severity = severity;
        this.line = line;
        this.column = column;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%s at line %d, column %d: %s", severity, line, column, message);
    }
}
