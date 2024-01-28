package com.ampaiva.hostfully.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PropertyDto(@Schema(description = "The ID of the Property") Long id,
                          @Schema(description = "The Property address (typically number followed by street") String address,
                          @Schema(description = "The city where Property is located") String city,
                          @Schema(description = "The state where Property is located") String state,
                          @Schema(description = "The country where Property is located") String country) {
}
