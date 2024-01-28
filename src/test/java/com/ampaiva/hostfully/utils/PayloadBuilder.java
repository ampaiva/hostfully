package com.ampaiva.hostfully.utils;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class PayloadBuilder {
    private final Map<String, Supplier<String>> fieldGenerators = new HashMap<>();

    public PayloadBuilder(Faker faker) {
        fieldGenerators.put("address", () -> faker.address().streetAddress());
        fieldGenerators.put("city", () -> faker.address().city());
        fieldGenerators.put("state", () -> faker.address().state());
        fieldGenerators.put("country", () -> faker.address().country());
    }

    private static boolean isNotIdOrNotNullable(DtoMetadata m) {
        return !("id".equals(m.name()) || m.nullable());
    }

    private static boolean isNotIdOrNullable(DtoMetadata m) {
        return !("id".equals(m.name()) || !m.nullable());
    }

    private String getFakeValue(String fieldName) {
        return fieldGenerators.getOrDefault(fieldName, () -> "?").get();
    }

    private String generateCreateKeyValues(List<DtoMetadata> list, Predicate<DtoMetadata> filteredFields) {
        return list.stream()
                .filter(filteredFields)
                .map(m -> "\"" + m.name() + "\":\"" + getFakeValue(m.name()) + "\"")
                .collect(Collectors.joining(","));
    }

    public String generateCreatePayload(List<DtoMetadata> list) {
        return "{" + generateCreateKeyValues(list, PayloadBuilder::isNotIdOrNotNullable) + "}";
    }

    public String generateCreateMissingNonNullablePayload(List<DtoMetadata> list) {
        return "{" + generateCreateKeyValues(list, PayloadBuilder::isNotIdOrNullable) + "}";
    }
}
