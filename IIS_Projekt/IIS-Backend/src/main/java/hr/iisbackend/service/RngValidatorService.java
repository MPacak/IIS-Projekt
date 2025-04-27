package hr.iisbackend.service;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import hr.iisbackend.model.ValidationResult;
import hr.iisbackend.model.XMLValidationError;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class RngValidatorService {

    public ValidationResult validateWithRNG(MultipartFile xmlFile, String rngPath) {
        List<XMLValidationError> errors = new ArrayList<>();

        try {
            // Load the RNG schema from resources
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(rngPath);
            if (schemaStream == null) {
                errors.add(new XMLValidationError("System", 0, 0, "RNG schema file not found: " + rngPath));
                return new ValidationResult(false, errors);
            }


            ErrorHandler handler = new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    errors.add(new XMLValidationError("Warning", exception.getLineNumber(),
                            exception.getColumnNumber(), exception.getMessage()));
                }

                @Override
                public void error(SAXParseException exception) {
                    errors.add(new XMLValidationError("Error", exception.getLineNumber(),
                            exception.getColumnNumber(), exception.getMessage()));
                }

                @Override
                public void fatalError(SAXParseException exception) {
                    errors.add(new XMLValidationError("Fatal", exception.getLineNumber(),
                            exception.getColumnNumber(), exception.getMessage()));
                }

            };

            PropertyMapBuilder propertyMapBuilder = new PropertyMapBuilder();
            propertyMapBuilder.put(ValidateProperty.ERROR_HANDLER, handler);
            PropertyMap propertyMap = propertyMapBuilder.toPropertyMap();

            // Create validation driver with our properties
            ValidationDriver driver = new ValidationDriver(propertyMap, propertyMap);
            // Load the schema
            InputSource schemaSource = new InputSource(schemaStream);
            if (!driver.loadSchema(schemaSource)) {
                errors.add(new XMLValidationError("System", 0, 0, "Failed to load RNG schema"));
                return new ValidationResult(false, errors);
            }

            // Validate the XML
            InputSource xmlSource = new InputSource(xmlFile.getInputStream());

            boolean isValid = driver.validate(xmlSource);

            return new ValidationResult(isValid, errors);

        } catch (SAXException e) {
            errors.add(new XMLValidationError("System", 0, 0, "Validation error: " + e.getMessage()));
            return new ValidationResult(false, errors);
        } catch (IOException e) {
            errors.add(new XMLValidationError("System", 0, 0, "I/O error: " + e.getMessage()));
            return new ValidationResult(false, errors);
        } catch (Exception e) {
            errors.add(new XMLValidationError("System", 0, 0, "Unexpected error: " + e.getMessage()));
            return new ValidationResult(false, errors);
        }
    }
}