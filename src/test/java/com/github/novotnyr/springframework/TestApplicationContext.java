package com.github.novotnyr.springframework;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.novotnyr.springframework.web.jsonschema.JsonRequestBodyArgumentResolverRegisteringBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan
public class TestApplicationContext extends WebMvcConfigurerAdapter {
    @Bean
    static BeanPostProcessor jsonRequestBodyArgumentResolverRegisteringBeanPostProcessor() {
        return new JsonRequestBodyArgumentResolverRegisteringBeanPostProcessor();
    }

    Jackson2ObjectMapperBuilder createJacksonObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .modules(new SimpleModule().addSerializer(ApiError.class, apiErrorJsonSerializer()));
    }

    ApiErrorJsonSerializer apiErrorJsonSerializer() {
        return new ApiErrorJsonSerializer();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(createJacksonObjectMapperBuilder().build()));
    }


}
