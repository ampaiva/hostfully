package com.ampaiva.hostfully.repository;

import com.ampaiva.hostfully.model.Property;

class RepositoryTestUtils {
    static Property generateRandomProperty() {
        Property property = new Property();
        property.setAddress("");
        property.setCity("");
        property.setState("");
        property.setCountry("");
        return property;
    }
}
