package com.ampaiva.hostfully.integration;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

@Component
public class DtoUtils {

    private Map<String, String> getDescriptions(Class<?> dtoClass) {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Schema.class))
                .collect(Collectors.toMap(Field::getName, field -> field.getAnnotation(Schema.class).description()));
    }
    public FieldDescriptor[] generateFieldExcept(Class<?> dtoClass, Set<String> excludedFields) {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .filter(field -> !excludedFields.contains(field.getName()))
                .map(field -> fieldWithPath(field.getName()).description(getDescriptions(dtoClass).get(field.getName())))
                .toArray(FieldDescriptor[]::new);
    }
    public ParameterDescriptor[] generateParameters(Class<?> dtoClass, Set<String> parameters) {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .filter(field -> parameters.contains(field.getName()))
                .map(field -> parameterWithName(field.getName()).description(getDescriptions(dtoClass).get(field.getName())))
                .toArray(ParameterDescriptor[]::new);
    }
    public String[] getFieldNamesExcept(Class<?> dtoClass, Set<String> excludedFields) {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .map(Field::getName)
                .filter(name -> !excludedFields.contains(name))
                .toArray(String[]::new);
    }

}
