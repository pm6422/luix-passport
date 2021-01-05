package org.infinity.passport.component;

import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * This bean post processor used to add the message attribute value implicitly and globally
 * for all validation annotations under the package of javax.validation.constraints.*
 * on the validated DTO's fields
 */
@Component
@Slf4j
public class ValidationBeanPostProcessor implements BeanPostProcessor {
    private static final String      VALIDATION_CODE_PREFIX = "Validation.";
    private static final Set<String> INITIALIZED_BEAN       = new HashSet<>();
    private static final String      MESSAGE_PROPERTY       = "message";
    private static final String      PATTERN_PACKAGE        = "javax.validation.constraints.Pattern";

    @Override
    public Object postProcessBeforeInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        if (!isControllerBean(bean)) {
            return bean;
        }

        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // Loop every method of Controller class
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                if (INITIALIZED_BEAN.contains(parameter.getType().getName()) || !isValidAnnotatedParameter(parameter)) {
                    // Skip initialized validation annotation class and non-validation annotation
                    continue;
                }
                Class<?> parameterType = parameter.getType();
                // Get all fields of the Class passed in or its super classes.
                Field[] parameterFields = FieldUtils.getAllFields(parameterType);
                for (Field parameterField : parameterFields) {
                    Annotation[] annotations = parameterField.getDeclaredAnnotations();
                    Optional<Annotation> apiModelPropertyAnnotation = Arrays.stream(annotations)
                            .filter(annotation -> annotation.annotationType().equals(ApiModelProperty.class)).findFirst();
                    for (Annotation annotation : annotations) {
                        addMessageProperty(parameterField, apiModelPropertyAnnotation, annotation);
                    }
                }
            }
        }
        return bean;
    }

    private void addMessageProperty(Field parameterField, Optional<Annotation> apiModelPropertyAnnotation, Annotation annotation) {
        String fieldAnnotationName = annotation.annotationType().getName();
        if (!isBeanValidationAnnotation(fieldAnnotationName)) {
            return;
        }

        try {
            // Enhance message property of validation annotation class by dynamic proxy
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            // Get the field memberValues of sun.reflect.annotationAnnotationInvocationHandler
            Field memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
            memberValuesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Object> annotationPropertyMap = (Map<String, Object>) memberValuesField.get(invocationHandler);
            String message = annotationPropertyMap.get(MESSAGE_PROPERTY).toString();

            // Get the bean validation annotation simple name
            String annotationSimpleName = fieldAnnotationName.substring(fieldAnnotationName.lastIndexOf(".") + 1);
            // Get field name from 'value' attribute of ApiModelProperty.class
            String annotationFieldName = apiModelPropertyAnnotation.map(value -> ((ApiModelProperty) value).value()).orElse("");
            String fieldName = StringUtils.isNotEmpty(annotationFieldName) ? annotationFieldName : "{" + parameterField.getName() + "}";
            if (PATTERN_PACKAGE.equals(fieldAnnotationName)) {
                // Handle message property of @Pattern annotation
                String msg = String.format("[%s]: %s", fieldName, message);
                annotationPropertyMap.put(MESSAGE_PROPERTY, msg);
                INITIALIZED_BEAN.add(parameterField.getDeclaringClass().getName());
            } else if (isDefaultMassage(message)) {
                // Handle message property of other validation annotations except for @Pattern
                // and message value of the annotation is null explicitly
                String msg = String.format("[%s]: {%s}", fieldName, VALIDATION_CODE_PREFIX.concat(annotationSimpleName));
                annotationPropertyMap.put(MESSAGE_PROPERTY, msg);
                INITIALIZED_BEAN.add(parameterField.getDeclaringClass().getName());
            } else {
                // Handle message property when message value of the annotation is NOT null explicitly
                String msg = String.format("[%s]: ", fieldName).concat(message);
                annotationPropertyMap.put(MESSAGE_PROPERTY, msg);
                INITIALIZED_BEAN.add(parameterField.getDeclaringClass().getName());
            }
            log.debug("Initialized validated bean class {}'s annotation @{} with message value: {}",
                    parameterField.getDeclaringClass().getSimpleName(),
                    annotationSimpleName,
                    annotationPropertyMap.get(MESSAGE_PROPERTY));
        } catch (Exception e) {
            log.error("Failed to process the bean validation annotation!", e);
        }
    }

    private boolean isControllerBean(Object bean) {
        return bean.getClass().isAnnotationPresent(Controller.class) || bean.getClass().isAnnotationPresent(RestController.class);
    }

    private boolean isValidAnnotatedParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(Valid.class) || parameter.isAnnotationPresent(Validated.class);
    }

    private boolean isBeanValidationAnnotation(String annotationName) {
        // Do NOT support "org.hibernate.validator.constraints" for now
        // || annotationName.startsWith("org.hibernate.validator.constraints")
        return annotationName.startsWith("javax.validation.constraints");
    }

    private boolean isDefaultMassage(String message) {
        return message.startsWith("{javax.validation.constraints");
    }
}