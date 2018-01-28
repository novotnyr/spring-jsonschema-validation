package com.github.novotnyr.springframework.web.jsonschema;

import com.github.novotnyr.springframework.web.jsonschema.annotation.JsonRequestBody;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonRequestBodyArgumentResolver implements HandlerMethodArgumentResolver {
    private RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;

    private ValidationExceptionMediator validationExceptionMediator = new ValidationExceptionMediator();

    public JsonRequestBodyArgumentResolver(RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor) {
        this.requestResponseBodyMethodProcessor = requestResponseBodyMethodProcessor;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object requestBodyAnnotatedReturnValue = this.requestResponseBodyMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        String name = Conventions.getVariableNameForParameter(parameter);
        BindingResult bindingResult = (BindingResult) mavContainer.getModel().get(BindingResult.MODEL_KEY_PREFIX + name);
        if (bindingResult == null) {
            bindingResult = createBindingResult(webRequest);
            mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX +  name, bindingResult);
        }
        validate(parameter, webRequest, bindingResult, isThrowingExceptionOnValidationError(parameter));

        return requestBodyAnnotatedReturnValue;
    }

    private BindingResult createBindingResult(WebRequest webRequest) {
        return new WebRequestBindingResult(webRequest);
    }

    private boolean isThrowingExceptionOnValidationError(MethodParameter parameter) {
        JsonRequestBody annotation = parameter.getParameterAnnotation(JsonRequestBody.class);
        return annotation.strict();
    }

    private void validate(MethodParameter parameter, NativeWebRequest webRequest, BindingResult bindingResult, boolean throwExceptionOnSchemaValidationError) throws IOException {
        int beforeSchemaValidationErrorCount = bindingResult.getErrorCount();
        String requestBodyJson = getJsonPayload(webRequest);

        Resource jsonSchemaResource = resolveJsonSchemaResource(parameter);
        validateRequestBody(requestBodyJson, jsonSchemaResource, bindingResult);

        if (bindingResult.getErrorCount() > beforeSchemaValidationErrorCount && throwExceptionOnSchemaValidationError) {
            throw new JsonSchemaValidationException(bindingResult);
        }
    }

    private Resource resolveJsonSchemaResource(MethodParameter methodParameter) {
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

    private void validateRequestBody(String json, Resource jsonSchemaResource, BindingResult bindingResult) throws JsonSchemaException {
        try {
            JSONObject rawSchema = new JSONObject(new JSONTokener(jsonSchemaResource.getInputStream()));
            JSONObject jsonObject = new JSONObject(new JSONTokener(json));

            SchemaLoader loader = SchemaLoader.builder()
                    .schemaJson(rawSchema)
                    .draftV6Support()
                    .build();
            Schema schema = loader.load().build();
            schema.validate(jsonObject);
        } catch (ValidationException e) {
            this.validationExceptionMediator.convert(e, bindingResult);
        } catch (IOException e) {
            throw new UnavailableJsonSchemaException(jsonSchemaResource, e);
        }
    }

    /**
     * Generates full classpath to the schema path, either using autodetection
     * or explicit value from annotation.
     */
    private String discoverSchemaPath(MethodParameter methodParameter) {
        JsonRequestBody annotation = methodParameter.getParameterAnnotation(JsonRequestBody.class);
        String schemaPath = annotation.schemaPath();
        if (! schemaPath.isEmpty()) {
            return "/" + schemaPath + ".json";
        } else {
            String declaringClassName = methodParameter.getDeclaringClass().getSimpleName().toLowerCase();
            String methodName = methodParameter.getMethod().getName();
            return "/" + declaringClassName + "#" + methodName + ".json";
        }
    }

    private String getJsonPayload(NativeWebRequest webRequest) throws IOException {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return StreamUtils.copyToString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonRequestBody.class);
    }

    /**
     * Represents an internal binding result mapped
     * over an existing HTTP request with JSON body.
     */
    private static class WebRequestBindingResult extends AbstractBindingResult {

        private final WebRequest webRequest;

        protected WebRequestBindingResult(WebRequest webRequest) {
            super("request");
            this.webRequest = webRequest;
        }

        @Override
        public WebRequest getTarget() {
            return this.webRequest;
        }

        @Override
        protected Object getActualFieldValue(String field) {
            return this.webRequest.getParameter(field);
        }
    }
}
