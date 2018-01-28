package com.github.novotnyr.springframework.web.jsonschema;

import org.everit.json.schema.ValidationException;
import org.springframework.validation.Errors;

public class ValidationExceptionMediator {
    public void convert(ValidationException validationException, Errors errors) {
        if("exclusiveMinimum".equals(validationException.getKeyword())) {
            String schemaLocation = validationException.getSchemaLocation();
            String field = schemaLocation.substring(schemaLocation.lastIndexOf("/") + 1);
            errors.rejectValue(field, "exclusive-minimum", validationException.getErrorMessage());
        }
        if("required".equals(validationException.getKeyword())) {
            String schemaLocation = validationException.getSchemaLocation();
            parseRequired(validationException, errors);
        }
        for (ValidationException nestedException : validationException.getCausingExceptions()) {
            if(isRequired(nestedException)) {
                parseRequired(nestedException, errors);
                continue;
            }
            errors.reject("reject", validationException.getErrorMessage());
        }
    }

    private boolean isRequired(ValidationException exception) {
        return exception.getErrorMessage().startsWith("required key");
    }

    private void parseRequired(ValidationException exception, Errors errors) {
        String message = exception.getMessage();
        int leftBracket = message.indexOf("[");
        int rightBracket = message.indexOf("]");
        String field = message.substring(leftBracket + 1, rightBracket);

        errors.rejectValue(field, "required-field", "Field is required");
    }
}
