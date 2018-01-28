package com.github.novotnyr.springframework.web.jsonschema;

import org.springframework.core.io.Resource;

public class UnavailableJsonSchemaException extends JsonSchemaException {

    public UnavailableJsonSchemaException(Resource jsonSchemaResource, Throwable cause) {
        super("Unable to load JSON schema from " + jsonSchemaResource.getDescription(), cause);
    }

    public UnavailableJsonSchemaException() {
    }

    public UnavailableJsonSchemaException(String msg) {
        super(msg);
    }

    public UnavailableJsonSchemaException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableJsonSchemaException(Throwable cause) {
        super(cause);
    }

}
