package com.ampaiva.hostfully.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record BlockDto(@Schema(description = "The ID of the block") Long id,
                       @Schema(description = "Start date of the block") LocalDate start,
                       @Schema(description = "End date of the block") LocalDate end,
                       @Schema(description = "The property blocked from reservation") PropertyDto property) {
}
