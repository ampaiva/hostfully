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

    @LocalServerPort
    int randomServerPort;

    @Value("${server.servlet.context-path}")
    String contextPath;

    int propertyId;

    int guestId;

    @BeforeEach
    public void setUp() {
        RestAssured.port = randomServerPort;
        RestAssured.basePath = contextPath;
        propertyId = getPropertyId();
        guestId = getGuestId();
    }

    @Test
    public void testCRUDProperty() {
        // Create
        int propertyId = getPropertyId();

        // Retrieve
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/property/" + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(propertyId));

        // Update
        given()
                .contentType(ContentType.JSON)
                .body("{ \"city\": \"Miami\"}")
                .when()
                .put("/api/property/" + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("city", equalTo("Miami"));
        // Patch
        given()
                .contentType(ContentType.JSON)
                .body("{ \"country\": \"France\"}")
                .when()
                .patch("/api/property/" + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("country", equalTo("France"));
        // Delete
        given()
                .contentType(ContentType.JSON)
                .body("{ \"country\": \"France\"}")
                .when()
                .delete("/api/property/" + propertyId)
                .then()
                .statusCode(HttpStatus.OK.value());
        // Retrieve Failed

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/property/" + propertyId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testCreateBooking() {

        int bookingId = given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-11\", \"end\": \"2024-01-12\", \"guest\": { \"id\": 1 }, \"property\": { \"id\": 1 } }")
                .when()
                .post("/api/booking")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

    }

    private static int getGuestId() {
        int guestId = given()
                .contentType(ContentType.JSON)
                .body("{ \"name\": \"Mrs. Lydia Cassin\", \"email\": \"Kaylah.Mueller92@raegan.name\", \"phone\": \"961-258-9402\", \"address\": \"Copacabana Beach, 1000\", \"city\": \"Rio de Janeiro\", \"state\": \"RJ\", \"country\": \"Brazil\" }")
                .when()
                .post("/api/guest")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        return guestId;
    }

    private static int getPropertyId() {
        int propertyId = given()
                .contentType(ContentType.JSON)
                .body("{ \"address\": \"Disney Road, 2024\", \"city\": \"Orlando\", \"state\": \"FL\", \"country\": \"USA\" }")
                .when()
                .post("/api/property")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        return propertyId;
    }


}
