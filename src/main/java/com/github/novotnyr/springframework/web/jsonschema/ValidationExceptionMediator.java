package com.github.novotnyr.springframework.web.jsonschema;

import org.everit.json.schema.ValidationException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.Errors;

/**
 * Converts {@link ValidationException} from Everit JSON Schema
 * to Spring {@link Errors} object.
 * <p>
 *     Since the {@link Errors} instance might already
 *     contain some errors, we cannot use the {@link Converter} API.
 *     Instead, we will contribute errors to the instance
 *     provided in the second parameter.
 * </p>
 */
public class ValidationExceptionMediator {
    /**
     * Does the best effort to convert the validation exception
     * to errors that will be contributed to the {@link Errors} instance
     * @param validationException Everit JSON schema validation errors
     * @param errors binding errors that will be used in Spring MVC mechanism
     */
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
