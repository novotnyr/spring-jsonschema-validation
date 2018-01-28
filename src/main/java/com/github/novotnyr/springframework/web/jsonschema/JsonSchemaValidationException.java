package com.github.novotnyr.springframework.web.jsonschema;

import org.springframework.validation.Errors;

/**
 * Indicates a failed JSON schema validation with corresponding
 * validation errors.
 */
public class JsonSchemaValidationException extends JsonSchemaException {
    private Errors errors;

    public JsonSchemaValidationException(Errors errors) {
        this.errors = errors;
    }

    public Errors getBindingResult() {
        return this.errors;
    }
}

