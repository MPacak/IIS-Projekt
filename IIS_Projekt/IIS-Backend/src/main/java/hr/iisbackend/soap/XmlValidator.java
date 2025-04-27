package hr.iisbackend.soap;
import hr.iisbackend.model.ValidationResult;
import hr.iisbackend.model.XMLValidationError;
import jakarta.xml.bind.*;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

public class XmlValidator {
    public static ValidationResult validateXmlWithJaxb(File xmlFile, File xsdFile) {
        List<XMLValidationError> errors = new ArrayList<>();
        try {
            // 1. Create JAXBContext for the wrapper class (SearchResponse)
            JAXBContext jaxbContext = JAXBContext.newInstance(SearchResponse.class);

            // 2. Create Unmarshaller
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // 3. Set Schema for validation
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);
            unmarshaller.setSchema(schema);
            // 4. Set custom ValidationEventHandler to collect errors
            unmarshaller.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(ValidationEvent event) {
                    ValidationEventLocator locator = event.getLocator();
                    XMLValidationError error = new XMLValidationError(
                            event.getSeverity() == ValidationEvent.WARNING ? "Warning" :
                                    event.getSeverity() == ValidationEvent.ERROR ? "Error" : "Fatal",
                            locator.getLineNumber(),
                            locator.getColumnNumber(),
                            event.getMessage()
                    );
                    errors.add(error);
                    // Return true to continue gathering more errors
                    return true;
                }
            });

            // 5. Try to unmarshal (it will validate during unmarshal)
            unmarshaller.unmarshal(xmlFile);
            boolean isValid = errors.isEmpty();
            System.out.println("XML is valid according to the XSD!");
            return new ValidationResult(isValid, errors);

        } catch (SAXException e) {
            errors.add(new XMLValidationError("SchemaError", 0, 0, "Schema load error: " + e.getMessage()));
            return new ValidationResult(false, errors);
        } catch (Exception e) {
            errors.add(new XMLValidationError("SystemError", 0, 0, "System error: " + e.getMessage()));
            return new ValidationResult(false, errors);
        }
    }
}
