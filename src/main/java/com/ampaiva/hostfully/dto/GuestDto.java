package com.ampaiva.hostfully.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GuestDto(@Schema(description = "The ID of the guest") Long id,
                       @Schema(description = "The name of the guest") String name,
                       @Schema(description = "The guest email") String email,
                       @Schema(description = "The guest phone number", nullable = true) String phone,
                       @Schema(description = "The guest home address (typically number followed by street") String address,
                       @Schema(description = "The city where guest is located") String city,
                       @Schema(description = "The state where guest is located") String state,
                       @Schema(description = "The country where guest is located") String country) {

}
