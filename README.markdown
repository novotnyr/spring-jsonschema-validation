Spring MVC JSON Schema Validation
=================================

Validate Spring MVC Controller parameters against JSON schema.
Just annotate them with `@JsonRequestBody`.

How does it work?
-----------------
By adding `@JsonRequestBody` to the controller method parameter,
we will be able to validate the corresponding parameter against JSON Schema.

    @RequestMapping(method = RequestMethod.POST, value = "/boxes")
    public void register(@JsonRequestBody BoxRequest request)

By convention, the `request` parameter will be validated against
JSON schema available in the `CLASSPATH`, in the `BoxController#register.json`
file.

Any schema validation error will be converted to the `JsonSchemaValidationException`
which wraps standard Spring `Errors` instance. Such exception
can be converted to the proper response by `@ExceptionHandler` mechanism
and like.

Configuring
-----------
To enable this facility, register the `JsonRequestBodyArgumentResolverRegisteringBeanPostProcessor`
bean in the application context.

    @Bean
    static BeanPostProcessor jsonRequestBodyArgumentResolverRegisteringBeanPostProcessor() {
        return new JsonRequestBodyArgumentResolverRegisteringBeanPostProcessor();
    }

Note that the post processor bean method must be static, as per
Spring Framework requirements for bean postprocessors.

For details and complex example, see unit tests and `TestApplicationContext`.

Requirements
------------

* Spring Framework 4.2.0 or newer
* Everit JSON Schema validator

Advanced Usage
--------------

### Combining JSON Schema validation and Spring MVC Validators

JSON Schema validation can be combined with the standard Spring MVC
validation. Just pass the `Errors` instance as the following
parameter and suppress the immediate validation exception throwing:

    @RequestMapping(method = RequestMethod.POST, value = "/boxes")
    public void register(@JsonRequestBody(strict = false) BoxRequest request, Errors errors)

By setting `strict` parameter to false, no validation exception will be thrown.
Instead, all validation exceptions will be converted to the `Errors` validation
errors.

