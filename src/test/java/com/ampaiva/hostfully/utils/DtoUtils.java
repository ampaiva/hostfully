package com.ampaiva.hostfully.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

@Component
public class DtoUtils {

    private DtoMetadata getMetadata(Field field) {
        if (!field.isAnnotationPresent(Schema.class)) {
            return new DtoMetadata(field.getName(), null, null);
        }
        Schema schema = field.getAnnotation(Schema.class);
        return new DtoMetadata(field.getName(), schema.description(), schema.nullable());
    }

    public List<DtoMetadata> getDtoMetadata(Class<?> dtoClass) {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .map(this::getMetadata)
                .collect(Collectors.toList());
    }

    public FieldDescriptor[] generateGetFieldDescriptors(List<DtoMetadata> listDtoMetadata) {
        return listDtoMetadata.stream()
                .map(dtoMetadata -> fieldWithPath(dtoMetadata.name()).description(dtoMetadata.description()))
                .toArray(FieldDescriptor[]::new);
    }

    public FieldDescriptor[] generateCreateFieldDescriptors(List<DtoMetadata> listDtoMetadata) {
        return listDtoMetadata.stream()
                .filter(dtoMetadata -> !"id".equals(dtoMetadata.name()))
                .map(dtoMetadata -> fieldWithPath(dtoMetadata.name()).description(dtoMetadata.description()))
                .toArray(FieldDescriptor[]::new);
    }

    public FieldDescriptor[] generateMissingFieldDescriptors(List<DtoMetadata> listDtoMetadata) {
        return listDtoMetadata.stream()
                .filter(dtoMetadata -> !("id".equals(dtoMetadata.name()) || !dtoMetadata.nullable()))
                .map(dtoMetadata -> fieldWithPath(dtoMetadata.name()).description(dtoMetadata.description()))
                .toArray(FieldDescriptor[]::new);
    }

    public ParameterDescriptor[] generateIdParameter(List<DtoMetadata> listDtoMetadata) {
        return listDtoMetadata.stream()
                .filter(dtoMetadata -> "id".equals(dtoMetadata.name()))
                .map(dtoMetadata -> parameterWithName(dtoMetadata.name()).description(dtoMetadata.description()))
                .toArray(ParameterDescriptor[]::new);
    }
}
