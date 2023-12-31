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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HostfullyIntegrationTest {

    public static final String API_GUEST = "/api/guest/";
    public static final String API_PROPERTY = "/api/property/";
    public static final String API_BOOKING = "/api/booking";
    @LocalServerPort
    int randomServerPort;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @BeforeEach
    public void setUp() {
        RestAssured.port = randomServerPort;
        RestAssured.basePath = contextPath;
    }


    private static int getGuestId() {
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"name\": \"Mrs. Lydia Cassin\", \"email\": \"Kaylah.Mueller92@raegan.name\", \"phone\": \"961-258-9402\", \"address\": \"Copacabana Beach, 1000\", \"city\": \"Rio de Janeiro\", \"state\": \"RJ\", \"country\": \"Brazil\" }")
                .when()
                .post("/api/guest")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private static int getPropertyId() {
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"address\": \"Disney Road, 2024\", \"city\": \"Orlando\", \"state\": \"FL\", \"country\": \"USA\" }")
                .when()
                .post("/api/property")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }


    @Test
    public void testCRUDProperty() {
        // Create
        int propertyId = getPropertyId();

        // Retrieve
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_PROPERTY + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(propertyId));

        // Update
        given()
                .contentType(ContentType.JSON)
                .body("{ \"city\": \"Miami\"}")
                .when()
                .put(API_PROPERTY + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("city", equalTo("Miami"));

        // Patch
        given()
                .contentType(ContentType.JSON)
                .body("{ \"country\": \"France\"}")
                .when()
                .patch(API_PROPERTY + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("country", equalTo("France"));

        // Delete
        given()
                .contentType(ContentType.JSON)
                .body("{ \"country\": \"France\"}")
                .when()
                .delete(API_PROPERTY + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value());

        // Retrieve Failed
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_PROPERTY + propertyId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testCRUDGuest() {
        // Create
        int guestId = getGuestId();

        // Retrieve
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_GUEST + guestId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(guestId));

        // Update
        given()
                .contentType(ContentType.JSON)
                .body("{ \"city\": \"Miami\"}")
                .when()
                .put(API_GUEST + guestId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("city", equalTo("Miami"));

        // Patch
        given()
                .contentType(ContentType.JSON)
                .body("{ \"country\": \"France\"}")
                .when()
                .patch(API_GUEST + guestId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("country", equalTo("France"));

        // Delete
        given()
                .contentType(ContentType.JSON)
                .body("{ \"country\": \"France\"}")
                .when()
                .delete(API_GUEST + guestId)
                .then()
                .statusCode(HttpStatus.OK.value());

        // Retrieve Failed
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_GUEST + guestId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testCreateBooking() {

        int propertyId = getPropertyId();

        int guestId = getGuestId();

        // Create
        int bookingId = given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-11\", \"end\": \"2024-01-12\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": "+ propertyId + " } }")
                .when()
                .post(API_BOOKING)
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
