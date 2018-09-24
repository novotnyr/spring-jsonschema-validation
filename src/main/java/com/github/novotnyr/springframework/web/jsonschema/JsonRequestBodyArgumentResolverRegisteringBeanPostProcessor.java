package com.github.novotnyr.springframework.web.jsonschema;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers <code>{@link JsonRequestBodyArgumentResolver}</code>
 * to <code>{@link RequestMappingHandlerAdapter}</code>
 * to the proper place.
 * <p>
 *     Since {@link JsonRequestBodyArgumentResolver} depends
 *     on the Jackson <code>@RequestBody</code>
 *     argument resolver, we need to register this validating
 *     argument resolver <i>after</i> the regular resolver.
 * </p>
 * <p>
 *     Since default argument resolvers (including {@link RequestResponseBodyMethodProcessor})
 *     are not beans, the proper order of initialization
 *     is supported by using this {@link BeanPostProcessor}.
 * </p>
 */
public class JsonRequestBodyArgumentResolverRegisteringBeanPostProcessor implements BeanPostProcessor {

    private JsonSchemaResolver jsonSchemaResolver;

    /**
     * Creates a new post processor with the default {@link ParamNameJsonSchemaResolver}
     */
    public JsonRequestBodyArgumentResolverRegisteringBeanPostProcessor() {
        this.jsonSchemaResolver = new ParamNameJsonSchemaResolver();
    }

    /**
     * Creates a new post processor with the schema resolver passed as argument
     */
    public JsonRequestBodyArgumentResolverRegisteringBeanPostProcessor(JsonSchemaResolver jsonSchemaResolver) {
        this.jsonSchemaResolver = jsonSchemaResolver;
    }

    /**
     * Registers the JSON validating argument after {@link HandlerMethodArgumentResolver}.
     * @return the same bean, unchanged, unless it is {@link RequestMappingHandlerAdapter}.
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            RequestMappingHandlerAdapter handlerAdapter = (RequestMappingHandlerAdapter) bean;
            List<HandlerMethodArgumentResolver> argumentResolvers = handlerAdapter.getArgumentResolvers();
            List<HandlerMethodArgumentResolver> extendedArgumentResolverList = new ArrayList<>(argumentResolvers);
            for (int i = 0; i < argumentResolvers.size(); i++) {
                HandlerMethodArgumentResolver argumentResolver = argumentResolvers.get(i);
                if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                    JsonRequestBodyArgumentResolver jsonRequestBodyArgumentResolver
                            = new JsonRequestBodyArgumentResolver((RequestResponseBodyMethodProcessor) argumentResolver, jsonSchemaResolver);
                    extendedArgumentResolverList.add(i + 1, jsonRequestBodyArgumentResolver);
                    break;
                }
            }
            handlerAdapter.setArgumentResolvers(extendedArgumentResolverList);
        }
        return bean;
    }

    /**
     * Do not process beans in any special way before initialization.
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
