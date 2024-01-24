package com.ampaiva.hostfully.dto;

import java.time.LocalDate;

public record BlockDto(Long id, LocalDate start, LocalDate end, PropertyDto property) {
}
