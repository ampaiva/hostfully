package com.ampaiva.hostfully.utils;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class PayloadBuilder {
    private final Map<String, Supplier<String>> fieldGenerators = new HashMap<>();

    public PayloadBuilder(Faker faker) {
        fieldGenerators.put("name", () -> faker.name().fullName());
        fieldGenerators.put("email", () -> faker.internet().emailAddress());
        fieldGenerators.put("phone", () -> faker.phoneNumber().phoneNumber());
        fieldGenerators.put("address", () -> faker.address().streetAddress());
        fieldGenerators.put("city", () -> faker.address().city());
        fieldGenerators.put("state", () -> faker.address().state());
        fieldGenerators.put("country", () -> faker.address().country());
    }

    private boolean isNotId(DtoMetadata m) {
        return !"id".equals(m.name());
    }

    private boolean isNotIdOrNullable(DtoMetadata m) {
        return !("id".equals(m.name()) || !m.nullable());
    }

    private String getFakeValue(String fieldName) {
        return fieldGenerators.getOrDefault(fieldName, () -> "?").get();
    }

    private String getFakeValue(DtoMetadata dtoMetadata) {
        return getFakeValue(dtoMetadata.name());
    }

    private String generateCreateKeyValues(Map<String, String> fakeValues) {
        return fakeValues.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
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
