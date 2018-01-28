package com.github.novotnyr.springframework;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.github.novotnyr.springframework.web.jsonschema.JsonSchemaValidationException;
import com.github.novotnyr.springframework.web.jsonschema.UnavailableJsonSchemaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError handleJsonSchemaValidationException(JsonSchemaValidationException exception) {
        return ApiError.of(exception.getBindingResult());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) e.getCause();
            ApiError apiError = new ApiError();
            apiError.addFieldError(getField(invalidFormatException), "invalid-property", invalidFormatException.getOriginalMessage());
            return apiError;
        } else {
            return new ApiError().addGlobalError("payload", e.getMessage());
        }
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError handleUnavailableJsonSchemaException(UnavailableJsonSchemaException exception) {
        return new ApiError().addGlobalError("payload", "Internal validation error");
    }

    private String getField(InvalidFormatException exception) {
        List<JsonMappingException.Reference> path = exception.getPath();
        JsonMappingException.Reference lastComponent = path.get(path.size() - 1);
        return lastComponent.getFieldName();
    }

}
