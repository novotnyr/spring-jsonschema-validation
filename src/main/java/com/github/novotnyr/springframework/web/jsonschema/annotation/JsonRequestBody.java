package com.github.novotnyr.springframework.web.jsonschema.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRequestBody {
    boolean strict() default true;

    String schemaPath() default "";
}
