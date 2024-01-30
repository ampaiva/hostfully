package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.BlockDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;


public class BlockIntegrationTest extends BaseIntegrationTest {

    private static final String BLOCKS = "blocks";
    private static final String BOOKINGS = "bookings";

    BlockIntegrationTest() {
        super(BlockDto.class, "blocks", "block");
    }

    @Override
    public void testCreateWithMissingNonNullableFields() {

    }

    @Test
    public void testInvalidBlockDates() {

        int propertyId = getPropertyId();

        // Create
        givenDoc(getDocument(getPostIdentifier(HttpStatus.BAD_REQUEST.value())))
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-19\", \"end\": \"2024-01-18\", \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BLOCKS)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testConflictWithExistingBooking() {

        int guestId = getGuestId();

        int propertyId = getPropertyId();

        // Create
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-11\", \"end\": \"2024-01-19\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BOOKINGS)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Conflict if there is a booking conflicting with the dates
        givenDoc(getDocument(getPostIdentifier(HttpStatus.CONFLICT.value())))
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BLOCKS)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void testConflictWithExistingBlock() {

        int propertyId = getPropertyId();

        // Block Dates
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-13\", \"end\": \"2024-01-20\", \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BLOCKS)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        // Conflict if there is a block intersecting with the dates
        givenDoc(getDocument(getPostIdentifier(HttpStatus.CONFLICT.value()) + "-2"))
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BLOCKS)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }
}
