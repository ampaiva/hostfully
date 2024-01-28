package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.PropertyDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class PropertyIntegrationTest extends BaseIntegrationTest {

    PropertyIntegrationTest() {
        super(PropertyDto.class, "properties");
    }


    @Test
    public void testUpdateProperty() {
        // Create
        int propertyId = getPropertyId();

        // Update
        given(this.spec)
                .filter(document("hostfully/property/put/" + HttpStatus.OK.value(), getPreprocessor(),
                        pathParameters(dtoUtils.generateParameters(PropertyDto.class, Set.of("id")))))
                .contentType(ContentType.JSON)
                .body("{ \"city\": \"Miami\"}")
                .when()
                .put(API_PROPERTY + "/{id}", propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("city", equalTo("Miami"));
    }

    @Test
    public void testUpdateNonExistingProperty() {
        // Create
        int nonExistingPropertyId = Integer.MAX_VALUE;

        // Update
        given(this.spec)
                .filter(document("hostfully/property/put/" + HttpStatus.NOT_FOUND.value(), getPreprocessor(),
                        pathParameters(dtoUtils.generateParameters(PropertyDto.class, Set.of("id")))))
                .contentType(ContentType.JSON)
                .body("{ \"city\": \"Miami\"}")
                .when()
                .put(API_PROPERTY + "/{id}", nonExistingPropertyId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Object with id=" + nonExistingPropertyId + " not found"));
    }

    @Test
    public void testPatchProperty() {
        // Create
        int propertyId = getPropertyId();

        // Patch
        given(this.spec)
                .filter(document("hostfully/property/patch/" + HttpStatus.OK.value(), getPreprocessor(),
                        pathParameters(dtoUtils.generateParameters(PropertyDto.class, Set.of("id")))))
                .contentType(ContentType.JSON)
                .body("{ \"country\": \"France\"}")
                .when()
                .patch(API_PROPERTY + "/{id}", propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("country", equalTo("France"));
    }

    @Test
    public void testPatchNonExistingProperty() {
        // Create
        int nonExistingPropertyId = Integer.MAX_VALUE;

        // Update
        given(this.spec)
                .filter(document("hostfully/property/patch/" + HttpStatus.NOT_FOUND.value(), getPreprocessor(),
                        pathParameters(dtoUtils.generateParameters(PropertyDto.class, Set.of("id")))))
                .contentType(ContentType.JSON)
                .body("{ \"city\": \"Miami\"}")
                .when()
                .patch(API_PROPERTY + "/{id}", nonExistingPropertyId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Object with id=" + nonExistingPropertyId + " not found"));
    }

    @Test
    public void testDeleteProperty() {
        // Create
        int propertyId = getPropertyId();

        // Delete
        given(this.spec)
                .filter(document("hostfully/property/delete/" + HttpStatus.NO_CONTENT.value(), getPreprocessor(),
                        pathParameters(dtoUtils.generateParameters(PropertyDto.class, Set.of("id")))))
                .contentType(ContentType.JSON)
                .when()
                .delete(API_PROPERTY + "/{id}", propertyId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Retrieve Failed
        given(this.spec).filter(document("hostfully/property/delete/" + HttpStatus.NOT_FOUND.value(), getPreprocessor(),
                        pathParameters(dtoUtils.generateParameters(PropertyDto.class, Set.of("id")))))
                .contentType(ContentType.JSON)
                .when()
                .delete(API_PROPERTY + "/{id}", propertyId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Object with id=" + propertyId + " not found"));
    }
}
