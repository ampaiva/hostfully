package com.ampaiva.hostfully.utils;

public record DtoMetadata(String name, String description, Boolean nullable) {
    public DtoMetadata {
        if (description == null) description = "";
        if (nullable == null) nullable = true;
    }
}
