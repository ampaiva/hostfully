package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.PropertyDto;

public class PropertyIntegrationTest extends BaseIntegrationTest {

    PropertyIntegrationTest() {
        super(PropertyDto.class, "properties", "property");
    }
}
