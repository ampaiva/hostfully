package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.exception.ConflictException;
import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.model.Booking;
import com.ampaiva.hostfully.model.Guest;
import com.ampaiva.hostfully.model.Property;
import com.ampaiva.hostfully.repository.BlockRepository;
import com.ampaiva.hostfully.repository.BookingRepository;
import com.ampaiva.hostfully.repository.GuestRepository;
import com.ampaiva.hostfully.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class BookingService extends BaseService<Booking> {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final GuestRepository guestRepository;
    private final BlockRepository blockRepository;

    public BookingService(BookingRepository bookingRepository, PropertyRepository propertyRepository, GuestRepository guestRepository, BlockRepository blockRepository) {
        super(bookingRepository);
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.guestRepository = guestRepository;
        this.blockRepository = blockRepository;
    }


    public Booking saveEntity(Booking booking) {
        Property property = propertyRepository.findById(booking.getProperty().getId()).orElseThrow();
        Guest guest = guestRepository.findById(booking.getGuest().getId()).orElseThrow();
        checkConflictingBookings(booking, property);
        checkConflictingBlock(booking, property);
        Booking savedEntity = super.saveEntity(booking);
        savedEntity.setProperty(property);
        savedEntity.setGuest(guest);
        return savedEntity;
    }

    private void checkConflictingBookings(Booking booking, Property property) {
        List<Booking> bookings = bookingRepository.findConflictingBookings(property, booking.getCanceled(), booking.getStart(), booking.getEnd());
        bookings.removeIf(b -> Objects.equals(b.getId(), booking.getId()));
        if (!bookings.isEmpty())
            throw new ConflictException("There are bookings for this property between " + booking.getStart() + " and " + booking.getEnd());
    }
    private void checkConflictingBlock(Booking booking, Property property) {
        List<Block> bookings = blockRepository.findBlocksBy(property, booking.getStart(), booking.getEnd());
        if (!bookings.isEmpty())
            throw new ConflictException("There are blocks for this property between " + booking.getStart() + " and " + booking.getEnd());
    }

    protected Booking applyPatch(Booking booking, Map<String, Object> updates) {
        if (updates.containsKey("start")) {
            booking.setStart(LocalDate.parse((String) updates.get("start")));
        }
        if (updates.containsKey("end")) {
            booking.setEnd(LocalDate.parse((String) updates.get("end")));
        }
        if (updates.containsKey("canceled")) {
            booking.setCanceled((Boolean) updates.get("canceled"));
        }
        return booking;
    }
}
