package com.ampaiva.hostfully.dto;

import java.time.LocalDate;

public record BookingDto(Long id, LocalDate start, LocalDate end, Boolean canceled, GuestDto guest,
                         PropertyDto property) {
    public BookingDto {
        canceled = canceled != null ? canceled : false;
    }
}
