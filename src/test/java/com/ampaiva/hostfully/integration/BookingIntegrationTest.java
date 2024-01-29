package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.BookingDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class BookingIntegrationTest extends BaseIntegrationTest {

    public static final String CANCEL = "/cancel";
    public static final String REBOOK = "/rebook";
    private static final String BOOKINGS = "bookings";

    BookingIntegrationTest() {
        super(BookingDto.class, BOOKINGS, "booking");
    }

    @Override
    public void testCreateWithMissingNonNullableFields() {

    }

    @Test
    public void testConflictWithIntersectingBooking() {

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
        givenDoc(getDocument(getPostIdentifier(HttpStatus.CREATED.value())))
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BOOKINGS)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void testInvalidDatesBooking() {

        int propertyId = getPropertyId();

        int guestId = getGuestId();

        // Create
        givenDoc(getDocument(getPostIdentifier(HttpStatus.BAD_REQUEST.value())))
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-19\", \"end\": \"2024-01-18\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BOOKINGS)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
