package com.ampaiva.hostfully.repository;

import com.ampaiva.hostfully.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
