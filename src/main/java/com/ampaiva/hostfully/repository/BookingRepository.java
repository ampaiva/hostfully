package com.ampaiva.hostfully.repository;

import com.ampaiva.hostfully.model.Booking;
import com.ampaiva.hostfully.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT e FROM Booking e WHERE e.property = :property AND :canceled = false AND e.canceled = false AND " +
            "((:start between e.start AND e.end) OR " +
            "(:end between e.start AND e.end) OR " +
            "(e.start between :start AND :end) OR " +
            "(e.end between :start AND :end))")
    List<Booking> findConflictingBookings(Property property, Boolean canceled, LocalDate start, LocalDate end);
}
