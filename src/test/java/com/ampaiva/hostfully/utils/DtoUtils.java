package com.ampaiva.hostfully.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

@Component
public class DtoUtils {

    private static FieldDescriptor[] getFieldDescriptors(List<DtoMetadata> listDtoMetadata, String parent) {
        return listDtoMetadata.stream()
                .map(dtoMetadata -> fieldWithPath(parent + dtoMetadata.name()).description(dtoMetadata.description()))
                .toArray(FieldDescriptor[]::new);
    }

    private DtoMetadata getMetadata(Field field) {
        if (!field.isAnnotationPresent(Schema.class)) {
            return new DtoMetadata(field.getName(), field.getType(), null, null);
        }
        Schema schema = field.getAnnotation(Schema.class);
        return new DtoMetadata(field.getName(), field.getType(), schema.description(), schema.nullable());
    }

    public List<DtoMetadata> getDtoMetadata(Class<?> dtoClass) {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .map(this::getMetadata)
                .collect(Collectors.toList());
    }

    public List<DtoMetadata> getDtoMetadataOfId(Class<?> dtoClass) {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .filter(field -> "id".equals(field.getName()))
                .map(this::getMetadata)
                .collect(Collectors.toList());
    }

    private FieldDescriptor[] getNestedFieldDescriptions(List<DtoMetadata> listDtoMetadata) {
        var nestedFields = getFieldDescriptors(listDtoMetadata);

        return Arrays.stream(nestedFields)
                .flatMap(Arrays::stream)
                .toArray(FieldDescriptor[]::new);
    }

    private FieldDescriptor[] getNestedFieldDescriptionsOfId(List<DtoMetadata> listDtoMetadata) {
        var nestedFields = getFieldDescriptorsOfId(listDtoMetadata);

        return Arrays.stream(nestedFields)
                .flatMap(Arrays::stream)
                .toArray(FieldDescriptor[]::new);
    }

    private FieldDescriptor[][] getFieldDescriptors(List<DtoMetadata> listDtoMetadata) {
        return listDtoMetadata.stream()
                .filter(dtoMetadata -> dtoMetadata.type().isRecord())
                .map(dtoMetadata -> generateFieldDescriptors(getDtoMetadata(dtoMetadata.type()), dtoMetadata.name() + "."))
                .toArray(FieldDescriptor[][]::new);
    }

    private FieldDescriptor[][] getFieldDescriptorsOfId(List<DtoMetadata> listDtoMetadata) {
        return listDtoMetadata.stream()
                .filter(dtoMetadata -> dtoMetadata.type().isRecord())
                .map(dtoMetadata -> generateFieldDescriptors(getDtoMetadataOfId(dtoMetadata.type()), dtoMetadata.name() + "."))
                .toArray(FieldDescriptor[][]::new);
    }

    public FieldDescriptor[] generateFieldDescriptors(List<DtoMetadata> listDtoMetadata, String parent) {
        FieldDescriptor[] rootFields = getFieldDescriptors(listDtoMetadata, parent);

        var normalized = getNestedFieldDescriptions(listDtoMetadata);

        return Stream.concat(Arrays.stream(rootFields), Arrays.stream(normalized))
                .toArray(FieldDescriptor[]::new);
    }

    public FieldDescriptor[] generateFieldDescriptorsOfId(List<DtoMetadata> listDtoMetadata, String parent) {
        FieldDescriptor[] rootFields = getFieldDescriptors(listDtoMetadata, parent);

        var normalized = getNestedFieldDescriptionsOfId(listDtoMetadata);

        return Stream.concat(Arrays.stream(rootFields), Arrays.stream(normalized))
                .toArray(FieldDescriptor[]::new);
    }

    public FieldDescriptor[] generateGetFieldDescriptors(List<DtoMetadata> listDtoMetadata) {
        return generateFieldDescriptors(listDtoMetadata, "");
    }

    public FieldDescriptor[] generateCreateFieldDescriptors(List<DtoMetadata> listDtoMetadata) {
        var listWithoutId = listDtoMetadata.stream()
                .filter(this::isNotId)
                .toList();
        return generateFieldDescriptorsOfId(listWithoutId, "");
    }

    private boolean isNotId(DtoMetadata dtoMetadata) {
        return !isId(dtoMetadata);
    }

    private boolean isRecordOrNullable(DtoMetadata dtoMetadata) {
        return dtoMetadata.type().isRecord() || dtoMetadata.nullable();
    }

    private boolean isId(DtoMetadata dtoMetadata) {
        return "id".equals(dtoMetadata.name());
    }

    public FieldDescriptor[] generateMissingFieldDescriptors(List<DtoMetadata> listDtoMetadata) {
        var listWithNullableOrRecord = listDtoMetadata.stream()
                .filter(this::isRecordOrNullable)
                .toList();
        return generateFieldDescriptorsOfId(listWithNullableOrRecord, "");
    }

    public ParameterDescriptor[] generateIdParameter(List<DtoMetadata> listDtoMetadata) {
        return listDtoMetadata.stream()
                .filter(dtoMetadata -> "id".equals(dtoMetadata.name()))
                .map(dtoMetadata -> parameterWithName(dtoMetadata.name()).description(dtoMetadata.description()))
                .toArray(ParameterDescriptor[]::new);
    }
}
