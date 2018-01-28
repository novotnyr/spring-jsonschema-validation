package com.github.novotnyr.springframework.web.jsonschema;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

public class JsonRequestBodyArgumentResolverRegisteringBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            RequestMappingHandlerAdapter handlerAdapter = (RequestMappingHandlerAdapter) bean;
            List<HandlerMethodArgumentResolver> argumentResolvers = handlerAdapter.getArgumentResolvers();
            List<HandlerMethodArgumentResolver> extendedArgumentResolverList = new ArrayList<>(argumentResolvers);
            for (int i = 0; i < argumentResolvers.size(); i++) {
                HandlerMethodArgumentResolver argumentResolver = argumentResolvers.get(i);
                if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                    JsonRequestBodyArgumentResolver jsonRequestBodyArgumentResolver = new JsonRequestBodyArgumentResolver((RequestResponseBodyMethodProcessor) argumentResolver);
                    extendedArgumentResolverList.add(i + 1, jsonRequestBodyArgumentResolver);
                    break;
                }
            }
            handlerAdapter.setArgumentResolvers(extendedArgumentResolverList);
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
