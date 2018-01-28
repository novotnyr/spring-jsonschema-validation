package com.github.novotnyr.springframework;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ApiError {
    private HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;

    private String code;

    private String message;

    private List<ObjectError> globalErrors = new ArrayList<>();

    private List<FieldError> fieldErrors = new ArrayList<>();

    public List<ObjectError> getGlobalErrors() {
        return this.globalErrors;
    }

    public List<FieldError> getFieldErrors() {
        return this.fieldErrors;
    }

    public ApiError addFieldErrors(Collection<? extends FieldError> fieldErrors) {
        this.fieldErrors.addAll(fieldErrors);

        return this;
    }

    public ApiError addFieldError(FieldError fieldError) {
        this.fieldErrors.add(fieldError);
        return this;
    }

    public ApiError addFieldError(String fieldName, String code, String message) {
        FieldError error = new FieldError("ApiError", fieldName, null, false, new String[]{code}, null, message);
        this.fieldErrors.add(error);

        return this;
    }

    public ApiError addGlobalError(String objectName, String detailedMessages) {
        this.globalErrors.add(new ObjectError(objectName, detailedMessages));
        return this;
    }

    public ApiError addGlobalError(ObjectError globalError) {
        this.globalErrors.add(globalError);
        return this;
    }

    public ApiError addGlobalErrors(Collection<? extends ObjectError> globalErrors) {
        this.globalErrors.addAll(globalErrors);
        return this;
    }

    public ApiError withHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getHttpStatusCode() {
        return httpStatus.value();
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ApiError of(Errors errors) {
        ApiError apiError = new ApiError();
        apiError.addGlobalErrors(errors.getGlobalErrors());
        apiError.addFieldErrors(errors.getFieldErrors());

        return apiError;
    }
}
