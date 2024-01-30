package com.ampaiva.hostfully.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record BookingDto(@Schema(description = "The ID of the booking") Long id,
                         @Schema(description = "Start date of the booking") LocalDate start,
                         @Schema(description = "End date of the booking") LocalDate end,
                         @Schema(description = "Flag indicating if the booking is canceled or not", nullable = true) Boolean canceled,
                         @Schema(description = "The guest that booked the property") GuestDto guest,
                         @Schema(description = "The property booked") PropertyDto property) {
    public BookingDto {
        canceled = canceled != null ? canceled : false;
    }
}
