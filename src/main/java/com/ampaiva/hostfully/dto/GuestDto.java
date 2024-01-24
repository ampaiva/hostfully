package com.ampaiva.hostfully.dto;

public record GuestDto(Long id, String name, String email, String phone, String address, String city, String state,
                       String country) {
}
