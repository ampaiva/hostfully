package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.GuestDto;

public class GuestIntegrationTest extends BaseIntegrationTest {

    GuestIntegrationTest() {
        super(GuestDto.class, "guests", "guest");
    }
}
