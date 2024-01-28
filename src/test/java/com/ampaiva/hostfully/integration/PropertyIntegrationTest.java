package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.PropertyDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class PropertyIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testCreateProperty() {
        // Create
        int propertyId = given(this.spec).filter(document("hostfully/property/post/201", preprocessRequest(modifyUris()
                                .scheme("https")
                                .host("com.ampaiva.hostfully")
                                .removePort()),
                        requestFields(DtoUtils.generateFieldDescriptors(PropertyDto.class, Set.of("id")))))
                .contentType(ContentType.JSON)
                .body("{ \"address\": \"Disney Road, 2024\", \"city\": \"Orlando\", \"state\": \"FL\", \"country\": \"USA\" }")
                .when()
                .post(API_PROPERTY)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        assertTrue(propertyId > 0);
    }

    private String getJsonRepr(Map.Entry<String, String> entry) {
        return String.format("\"%s\": \"%s\"", entry.getKey(), entry.getValue());
    }

    private String generateBody(Map<String, String> map) {
        var str = "{" + map.entrySet().stream()
                .map(this::getJsonRepr)
                .collect(Collectors.joining(",")) + "}";
        return str;
    }

    @ParameterizedTest
    @CsvSource({"address", "city", "state", "country"})
    public void testCreatePropertyWithoutAddress(String missingField) {
        Map<String, String> map = Map.of(
                "address", "1100 5th St",
                "city", "Tucson",
                "state", "AZ",
                "country", "USA"
        );

        var newMap = map.entrySet().stream()
                .filter(entry -> !missingField.equals(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));


        // Create
        given(this.spec).filter(document("hostfully/property/post/400", preprocessRequest(modifyUris()
                                .scheme("https")
                                .host("com.ampaiva.hostfully")
                                .removePort()),
                        requestFields(DtoUtils.generateFieldDescriptors(PropertyDto.class, Set.of("id", missingField)))))
                .contentType(ContentType.JSON)
                .body(generateBody(newMap))
                .when()
                .post(API_PROPERTY)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("not-null property references a null or transient value : com.ampaiva.hostfully.model.Property." + missingField));
    }

    @Test
    public void testCRUDProperty() {
        // Create
        int propertyId = getPropertyId();

        // Get
        given(this.spec)
                .filter(document("hostfully/property/get/id", preprocessRequest(modifyUris()
                                .scheme("https")
                                .host("com.ampaiva.hostfully")
                                .removePort()),
                        responseFields(
                                fieldWithPath("id").description("The property ID"),
                                fieldWithPath("address").description("The property address (typically number followed by street"),
                                fieldWithPath("city").description("The property city"),
                                fieldWithPath("state").description("The property state"),
                                fieldWithPath("country").description("The property country"))))
                .contentType(ContentType.JSON)
                .when()
                .get(API_PROPERTY + "/" + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(propertyId));

        // Get All
        given(this.spec)
                .filter(document("hostfully/property/get",
                        preprocessRequest(modifyUris()
                                .scheme("https")
                                .host("com.ampaiva.hostfully")
                                .removePort())))
                .contentType(ContentType.JSON)
                .when()
                .get(API_PROPERTY)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasItems(hasEntry("id", propertyId)));

        // Update
        given(this.spec)
                .filter(document("hostfully/property/put",
                        preprocessRequest(modifyUris()
                                .scheme("https")
                                .host("com.ampaiva.hostfully")
                                .removePort())))
                .contentType(ContentType.JSON)
                .body("{ \"city\": \"Miami\"}")
                .when()
                .put(API_PROPERTY + "/" + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("city", equalTo("Miami"));

        // Patch
        given()
                .contentType(ContentType.JSON)
                .body("{ \"country\": \"France\"}")
                .when()
                .patch(API_PROPERTY + "/" + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("country", equalTo("France"));

        // Delete
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete(API_PROPERTY + "/" + propertyId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Retrieve Failed
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_PROPERTY + "/" + propertyId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
