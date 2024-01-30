package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.BookingDto;
import com.ampaiva.hostfully.utils.DtoMetadata;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BookingIntegrationTest extends BaseIntegrationTest {

    public static final String CANCEL = "/cancel";
    public static final String REBOOK = "/rebook";
    private static final String BOOKINGS = "bookings";

    BookingIntegrationTest() {
        super(BookingDto.class, BOOKINGS, "booking");
    }

    private boolean filterCreateFields(DtoMetadata dtoMetadata) {
        return dtoUtils.isNotId(dtoMetadata) && !"canceled".equals(dtoMetadata.name());
    }

    @Override
    FieldDescriptor[] getFieldDescriptors() {
        return dtoUtils.generateCreateFieldDescriptors(getMetadata(), this::filterCreateFields);
    }

    @Override
    String getCreatePayload() {
        List<DtoMetadata> dtoMetadataList = getMetadata().stream()
                .filter(m -> !"canceled".equals(m.name())).toList();
        return payloadBuilder.generateCreatePayload(dtoMetadataList);
    }

    @Override
    public void testCreateWithMissingNonNullableFields() {

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
        givenDoc(getDocument(getPostIdentifier(HttpStatus.CONFLICT.value())))
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BOOKINGS)
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
                .post(API + "blocks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        int guestId = getGuestId();

        // Conflict if there is a block intersecting with the dates
        givenDoc(getDocument(getPostIdentifier(HttpStatus.CONFLICT.value()) + "-2"))
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"guest\": { \"id\": " + guestId + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BOOKINGS)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
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
                .post(API + BOOKINGS)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        // Conflict if there is a booking conflicting with the dates
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"guest\": { \"id\": " + getGuestId() + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BOOKINGS)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());

        // Cancel
        given()
                .contentType(ContentType.JSON)
                .when()
                .patch(API + BOOKINGS + "/" + bookingId + CANCEL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("canceled", equalTo(true));

        // Successful Booking due to previous cancel
        given()
                .contentType(ContentType.JSON)
                .body("{ \"start\": \"2024-01-12\", \"end\": \"2024-01-14\", \"guest\": { \"id\": " + getGuestId() + " }, \"property\": { \"id\": " + propertyId + " } }")
                .when()
                .post(API + BOOKINGS)
                .then()
                .statusCode(HttpStatus.CREATED.value());

    }


    @Test
    public void testCancelAndRebook() {

        // Create
        int bookingId = getBookingId();

        // Cancel
        givenDoc(getPatchIdentifier(HttpStatus.OK.value()) + "/cancel")
                .contentType(ContentType.JSON)
                .when()
                .patch(API + BOOKINGS + "/{id}" + CANCEL, bookingId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("canceled", equalTo(true));

        // Rebook
        givenDoc(getPatchIdentifier(HttpStatus.OK.value()) + "/rebook")
                .contentType(ContentType.JSON)
                .when()
                .patch(API + BOOKINGS + "/{id}" + REBOOK, bookingId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("canceled", equalTo(false));

    }
}
