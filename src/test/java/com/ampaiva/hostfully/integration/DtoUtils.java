package com.ampaiva.hostfully.integration;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class DtoUtils {

    public static FieldDescriptor[] generateFieldDescriptors(Class<?> dtoClass, Set<String> excludedFields) {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .filter(field -> !excludedFields.contains(field.getName())) // Exclude "id"
                .map(field -> fieldWithPath(field.getName()).description(getDescriptions(dtoClass).get(field.getName())))
                .toArray(FieldDescriptor[]::new);
    }

    public static Map<String, String> getDescriptions(Class<?> dtoClass) {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Schema.class))
                .collect(Collectors.toMap(Field::getName, field -> field.getAnnotation(Schema.class).description()));
    }
}
