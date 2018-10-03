package com.github.novotnyr.springframework.web.jsonschema;

import com.github.novotnyr.springframework.web.jsonschema.annotation.JsonRequestBody;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Json schema resolver that resolves based on controller method name
 */
public class ParamNameJsonSchemaResolver implements JsonSchemaResolver {
    public Resource resolveJsonSchemaResource(MethodParameter methodParameter, NativeWebRequest webRequest) {
        JsonRequestBody annotation = methodParameter.getParameterAnnotation(JsonRequestBody.class);
        String schemaPath = annotation.schemaPath();
        if (! schemaPath.isEmpty()) {
            return new ClassPathResource("/" + schemaPath + ".json");
        } else {
            String declaringClassName = methodParameter.getDeclaringClass().getSimpleName().toLowerCase();
            String methodName = methodParameter.getMethod().getName();
            return new ClassPathResource("/" + declaringClassName + "#" + methodName + ".json");
        }
    }
}

