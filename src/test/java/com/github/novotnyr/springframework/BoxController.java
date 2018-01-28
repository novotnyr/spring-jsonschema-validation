package com.github.novotnyr.springframework;

import com.github.novotnyr.springframework.web.jsonschema.annotation.JsonRequestBody;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoxController {
    @RequestMapping(method = RequestMethod.POST, value = "/boxes")
    public void register(@JsonRequestBody BoxRequest request) {
        System.err.println("Register " + request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/boxes", params = "laxly")
    public void registerLaxly(@JsonRequestBody(strict = false) BoxRequest request) {

    }

    @RequestMapping(method = RequestMethod.POST, value = "/boxes", params = {"errors"})
    public ApiError registerWithBodyAndErrors(@JsonRequestBody BoxRequest request, Errors errors) {
        Validator validator = new Validator() {

            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }

            @Override
            public void validate(Object target, Errors errors) {
                BoxRequest boxRequest = (BoxRequest) target;
                if (boxRequest.getName() != null && boxRequest.getName().startsWith("smelly")) {
                    errors.rejectValue("name", "nasty-box", "This is not a pretty content");
                }
            }
        };
        validator.validate(request, errors);

        return ApiError.of(errors);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/boxes", params = {"laxly", "errors"})
    public ApiError registerLaxlyWithBodyAndErrors(@JsonRequestBody(strict = false) BoxRequest request, Errors errors) {
        Validator validator = new Validator() {

            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }

            @Override
            public void validate(Object target, Errors errors) {
                BoxRequest boxRequest = (BoxRequest) target;
                if (boxRequest.getName() != null && boxRequest.getName().startsWith("smelly")) {
                    errors.rejectValue("name", "nasty-box", "This is not a pretty content");
                }
            }
        };
        validator.validate(request, errors);

        return ApiError.of(errors);
    }
}
