package com.ampaiva.hostfully.repository;

import com.ampaiva.hostfully.model.Booking;
import com.ampaiva.hostfully.model.Property;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @ParameterizedTest
    @CsvSource({"11, 12, 0",
            "11, 13, 1",
            "11, 21, 1",
            "13, 13, 1",
            "13, 19, 1",
            "13, 20, 1",
            "14, 19, 1",
            "14, 20, 1",
            "14, 21, 1",
            "20, 20, 1",
            "20, 21, 1",
            "21, 21, 0",
            "21, 22, 0"})
    void findConflictingBookingsWhenCanceledIsFalseAndChosenDateIntersectsExistingBooking(String start, String end, int expected) {
        Property property = RepositoryTestUtils.generateRandomProperty();
        ;
        Property otherProperty = RepositoryTestUtils.generateRandomProperty();
        ;
        propertyRepository.saveAll(List.of(property, otherProperty));

        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setStart(LocalDate.parse("2024-01-13"));
        booking.setEnd(LocalDate.parse("2024-01-20"));

        bookingRepository.save(booking);

        List<Booking> result;
        result = bookingRepository.findConflictingBookings(property, false, LocalDate.parse("2024-01-" + start), LocalDate.parse("2024-01-" + end));
        assertEquals(expected, result.size());

        result = bookingRepository.findConflictingBookings(property, true, LocalDate.parse("2024-01-" + start), LocalDate.parse("2024-01-" + end));
        // If the booking is canceled, it does not conflict with any other
        assertEquals(0, result.size());

        result = bookingRepository.findConflictingBookings(otherProperty, false, LocalDate.parse("2024-01-" + start), LocalDate.parse("2024-01-" + end));
        // if it is a different property doesn't cause conflicts
        assertEquals(0, result.size());

        booking.setCanceled(true);
        bookingRepository.save(booking);

        result = bookingRepository.findConflictingBookings(property, false, LocalDate.parse("2024-01-" + start), LocalDate.parse("2024-01-" + end));
        // Canceled bookings don't cause conflicts
        assertEquals(0, result.size());
    }
}
