package hr.iisbackend.soap;

import hr.iisbackend.model.XMLValidationError;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private boolean valid;
    private List<XMLValidationError> errors;
}