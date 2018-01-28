package com.github.novotnyr.springframework;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.IOException;

public class ApiErrorJsonSerializer extends JsonSerializer<ApiError> {
    @Override
    public void serialize(ApiError apiError, JsonGenerator json, SerializerProvider provider) throws IOException {
        // @formatter:off
        json.writeStartObject();
            json.writeObjectFieldStart("meta");
                json.writeNumberField("code", apiError.getHttpStatusCode());
            json.writeEndObject();

            json.writeObjectFieldStart("error");
                json.writeStringField("message", apiError.getMessage());
                json.writeStringField("code", apiError.getCode());
                json.writeObjectFieldStart("validation");
                    writeGlobalErrors(apiError, json);
                    writeFieldErrors(apiError, json);
                json.writeEndObject();
            json.writeEndObject();
        json.writeEndObject();

        // @formatter:on
    }

    private void writeGlobalErrors(ApiError apiError, JsonGenerator jsonGenerator) throws IOException {
        if (apiError.getGlobalErrors().isEmpty()) {
            return;
        }

        jsonGenerator.writeArrayFieldStart("global");
        for (ObjectError globalError : apiError.getGlobalErrors()) {
            writeGlobaError(jsonGenerator, globalError);
        }
        jsonGenerator.writeEndArray();
    }

    private void writeGlobaError(JsonGenerator jsonGenerator, ObjectError error) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("code", error.getCode());
        jsonGenerator.writeStringField("message", error.getDefaultMessage());
        jsonGenerator.writeEndObject();
    }


    private void writeFieldErrors(ApiError apiError, JsonGenerator jsonGenerator) throws IOException {
        if (apiError.getFieldErrors().isEmpty()) {
            return;
        }
        jsonGenerator.writeArrayFieldStart("field");
        for (FieldError fieldError : apiError.getFieldErrors()) {
            writeField(jsonGenerator, fieldError);
        }
        jsonGenerator.writeEndArray();
    }


    private void writeField(JsonGenerator jsonGenerator, FieldError fieldError) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", fieldError.getField());
        jsonGenerator.writeStringField("code", fieldError.getCode());
        jsonGenerator.writeStringField("message", fieldError.getDefaultMessage());
        jsonGenerator.writeEndObject();
    }
}
