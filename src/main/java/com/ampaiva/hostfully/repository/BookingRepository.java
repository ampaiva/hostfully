package com.ampaiva.hostfully.repository;

import com.ampaiva.hostfully.model.Booking;
import com.ampaiva.hostfully.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT e FROM Booking e WHERE e.property = :property and :canceled = false and e.canceled = false and ((e.start <= :start AND e.end >= :start) OR (e.start <= :end AND e.end >= :end))")
    List<Booking> findConflictingBookings(Property property, Boolean canceled, LocalDate start, LocalDate end);
}
