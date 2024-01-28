package com.ampaiva.hostfully.utils;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private String getFakeValue(String fieldName) {
        return fieldGenerators.getOrDefault(fieldName, () -> "?").get();
    }

    private String generateKeyValues(List<DtoMetadata> list) {
        return list.stream()
                .filter(m -> ! ("id".equals(m.name()) ||  m.nullable()))
                .map(m -> "\"" + m.name() + "\":\"" + getFakeValue(m.name()) + "\"")
                .collect(Collectors.joining(","));
    }

    public String generateCreatePayload(List<DtoMetadata> list) {
        // "{ \"address\": \"" + faker.address().streetAddress() + "Disney Road, 2024\", \"city\": \"Orlando\", \"state\": \"FL\", \"country\": \"USA\" }")
        return "{" + generateKeyValues(list) + "}";
    }
}
