package com.ampaiva.hostfully.integration;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HostfullyIntegrationTest {

    public static final String API_GUEST = "/api/guests";
    public static final String API_PROPERTY = "/api/properties";
    public static final String API_BLOCK = "/api/blocks";
    public static final String API_BOOKING = "/api/bookings";
    public static final String CANCEL = "/cancel";
    public static final String REBOOK = "/rebook";

    @LocalServerPort
    int randomServerPort;

    @Value("${server.servlet.context-path}")
    String contextPath;

    private static int getGuestId() {
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"name\": \"Mrs. Lydia Cassin\", \"email\": \"Kaylah.Mueller92@raegan.name\", \"phone\": \"961-258-9402\", \"address\": \"Copacabana Beach, 1000\", \"city\": \"Rio de Janeiro\", \"state\": \"RJ\", \"country\": \"Brazil\" }")
                .when()
                .post(API_GUEST)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");
    }

    private static int getPropertyId() {
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"address\": \"Disney Road, 2024\", \"city\": \"Orlando\", \"state\": \"FL\", \"country\": \"USA\" }")
                .when()
                .post(API_PROPERTY)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");
    }

    private static int getBookingId() {
        int propertyId = getPropertyId();

        int guestId = getGuestId();

        // Create
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-11\", \"end\": \"2024-01-19\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BOOKING)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = randomServerPort;
        RestAssured.basePath = contextPath;
    }


    @Test
    public void testCRUDBlock() {
        int propertyId = getPropertyId();

        // Create
        int blockId = given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-13\", \"end\": \"2024-01-20\", \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BLOCK)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        // Retrieve
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_BLOCK + "/" + blockId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(blockId));

        // Update
        given()
                .contentType(ContentType.JSON)
                .body("{ \"end\": \"2024-01-19\"}")
                .when()
                .put(API_BLOCK + "/" + blockId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("end", equalTo("2024-01-19"));

        // Patch
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\"}")
                .when()
                .patch(API_BLOCK + "/" + blockId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("start", equalTo("2024-01-12"));

        // Delete
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete(API_BLOCK + "/" + blockId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Retrieve Failed
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_BLOCK + "/" + blockId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testCRUDBooking() {

        // Create
        int bookingId = getBookingId();

        // Retrieve
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_BOOKING + "/" + bookingId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(bookingId));

        // Update
        given()
                .contentType(ContentType.JSON)
                .body("{ \"end\": \"2024-01-13\"}")
                .when()
                .put(API_BOOKING + "/" + bookingId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("end", equalTo("2024-01-13"));

        // Patch
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-09\"}")
                .when()
                .patch(API_BOOKING + "/" + bookingId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("start", equalTo("2024-01-09"));

        // Delete
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete(API_BOOKING + "/" + bookingId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Retrieve Failed
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_BOOKING + "/" + bookingId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testConflictWithIntersectingBooking() {

        int propertyId = getPropertyId();

        int guestId = getGuestId();

        // Create
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-11\", \"end\": \"2024-01-19\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BOOKING)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Conflict if there is a booking conflicting with the dates
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BOOKING)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void testInvalidDatesBooking() {

        int propertyId = getPropertyId();

        int guestId = getGuestId();

        // Create
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-19\", \"end\": \"2024-01-18\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BOOKING)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testInvalidDatesBlock() {

        int propertyId = getPropertyId();

        // Create
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-19\", \"end\": \"2024-01-18\", \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BLOCK)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testConflictWithExistingBlock() {

        int propertyId = getPropertyId();

        // Block Dates
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-13\", \"end\": \"2024-01-20\", \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BLOCK)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        int guestId = getGuestId();

        // Conflict if there is a block intersecting with the dates
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BOOKING)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void testCancelAndRebook() {

        // Create
        int bookingId = getBookingId();

        // Cancel
        given()
                .contentType(ContentType.JSON)
                .when()
                .patch(API_BOOKING + "/" + bookingId + CANCEL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("canceled", equalTo(true));

        // Rebook
        given()
                .contentType(ContentType.JSON)
                .when()
                .patch(API_BOOKING + "/" + bookingId + REBOOK)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("canceled", equalTo(false));

    }

    @Test
    public void testBookWhenPreviousBookingWasCanceled() {
        int propertyId = getPropertyId();

        int guestId = getGuestId();

        // Create
        int bookingId = given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-11\", \"end\": \"2024-01-19\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BOOKING)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        // Conflict if there is a booking conflicting with the dates
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"guest\": { \"id\": " + getGuestId() + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BOOKING)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());

        // Cancel
        given()
                .contentType(ContentType.JSON)
                .when()
                .patch(API_BOOKING + "/" + bookingId + CANCEL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("canceled", equalTo(true));

        // Successful Booking due to previous cancel
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"guest\": { \"id\": " + getGuestId() + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API_BOOKING)
                .then()
                .statusCode(HttpStatus.CREATED.value());

    }
}
