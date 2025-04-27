package hr.iisbackend.service;

import hr.iisbackend.model.ValidationResult;
import hr.iisbackend.model.XMLValidationError;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class XSDValidatorService {
    public ValidationResult validateWithXSD(MultipartFile xmlFile, String xsdPath) {
        List<XMLValidationError> errors = new ArrayList<>();
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            InputStream xsdStream = getClass().getClassLoader().getResourceAsStream(xsdPath);
            if (xsdStream == null) {
                errors.add(new XMLValidationError("System", 0, 0, "XSD file not found: " + xsdPath));
                return new ValidationResult(false, errors);
            }

            Schema schema = factory.newSchema(new StreamSource(xsdStream));
            Validator validator = schema.newValidator();

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    errors.add(new XMLValidationError("Warning", e.getLineNumber(), e.getColumnNumber(), e.getMessage()));
                }

                @Override
                public void error(SAXParseException e) {
                    errors.add(new XMLValidationError("Error", e.getLineNumber(), e.getColumnNumber(), e.getMessage()));
                }

                @Override
                public void fatalError(SAXParseException e) {
                    errors.add(new XMLValidationError("Fatal", e.getLineNumber(), e.getColumnNumber(), e.getMessage()));
                }
            });
            validator.validate(new StreamSource(xmlFile.getInputStream()));
            return new ValidationResult(errors.isEmpty(), errors);
        } catch (Exception e) {
            errors.add(new XMLValidationError("System", 0, 0, "Validation error: " + e.getMessage()));
            return new ValidationResult(false, errors);
        }
    }
}
