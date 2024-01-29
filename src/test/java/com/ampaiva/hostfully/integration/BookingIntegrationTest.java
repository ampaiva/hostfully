package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.BookingDto;


public class BookingIntegrationTest extends BaseIntegrationTest {

    BookingIntegrationTest() {
        super(BookingDto.class, "bookings", "booking");
    }

    @Override
    public void testGetNonExisting() {

    }

    @Override
    public void testCreateWithMissingNonNullableFields() {

    }

    @Override
    public void testPatch() {

    }
}
