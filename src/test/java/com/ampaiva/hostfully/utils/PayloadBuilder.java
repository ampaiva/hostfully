package com.ampaiva.hostfully.utils;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class PayloadBuilder {
    private final Map<String, Supplier<String>> fieldGenerators = new HashMap<>();

    public Supplier<Integer> getPropertyId;

    public PayloadBuilder(Faker faker) {
        fieldGenerators.put("address", () -> faker.address().streetAddress());
        fieldGenerators.put("city", () -> faker.address().city());
        fieldGenerators.put("country", () -> faker.address().country());
        fieldGenerators.put("email", () -> faker.internet().emailAddress());
        fieldGenerators.put("end", () -> formatDate(faker.date().future(10, 7, TimeUnit.DAYS)));
        fieldGenerators.put("name", () -> faker.name().fullName());
        fieldGenerators.put("phone", () -> faker.phoneNumber().phoneNumber());
        fieldGenerators.put("property", this::getProperty);
        fieldGenerators.put("start", () -> formatDate(faker.date().future(6, 4, TimeUnit.DAYS)));
        fieldGenerators.put("state", () -> faker.address().state());
    }

    private static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private String getProperty() {
        return "{\"id\": " + getPropertyId.get() + "}";
    }

    private boolean isNotId(DtoMetadata m) {
        return !"id".equals(m.name());
    }

    private boolean isNotIdAndNotRecord(DtoMetadata m) {
        return !("id".equals(m.name()) || m.type().isRecord());
    }

    private boolean isNotIdOrNullable(DtoMetadata m) {
        return m.type().isRecord() || !("id".equals(m.name()) || !m.nullable());
    }

    private String getFakeValue(String fieldName) {
        return fieldGenerators.getOrDefault(fieldName, () -> "?").get();
    }

    private String getFakeValue(DtoMetadata dtoMetadata) {
        return getFakeValue(dtoMetadata.name());
    }

    private String quoteValue(String fieldName, String value) {
        if ("property".equals(fieldName))
            return value;
        return "\"" + value + "\"";
    }

    private String generateCreateKeyValues(Map<String, String> fakeValues) {
        return fakeValues.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\":" + quoteValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(","));
    }


    private String generatePayload(String keyValues) {
        return "{" + keyValues + "}";
    }

    private Map<String, String> generateFakeValues(List<DtoMetadata> list, Predicate<DtoMetadata> filterFields) {
        return list.stream()
                .filter(filterFields)
                .filter(dtoMetadata -> fieldGenerators.containsKey(dtoMetadata.name()))
                .collect(Collectors.toMap(DtoMetadata::name, this::getFakeValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    public Map<String, String> generateCreateFakeValues(List<DtoMetadata> list) {
        return generateFakeValues(list, this::isNotId);
    }

    public Map<String, String> generateCreateFakeValuesFilterRelations(List<DtoMetadata> list) {
        return generateFakeValues(list, this::isNotIdAndNotRecord);
    }

    public String generateCreatePayload(Map<String, String> fakeValues) {
        return generatePayload(generateCreateKeyValues(fakeValues));
    }

    public String generateCreatePayload(List<DtoMetadata> list) {
        return generateCreatePayload(generateCreateFakeValues(list));
    }

    public String generateCreateMissingNonNullablePayload(List<DtoMetadata> list) {
        return generatePayload(generateCreateKeyValues(generateFakeValues(list, this::isNotIdOrNullable)));
    }
}
