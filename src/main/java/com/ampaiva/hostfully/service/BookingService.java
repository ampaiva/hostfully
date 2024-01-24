package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.dto.BookingDto;
import com.ampaiva.hostfully.exception.ConflictException;
import com.ampaiva.hostfully.exception.PatchException;
import com.ampaiva.hostfully.mapper.BookingMapper;
import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.model.Booking;
import com.ampaiva.hostfully.model.Guest;
import com.ampaiva.hostfully.model.Property;
import com.ampaiva.hostfully.repository.BlockRepository;
import com.ampaiva.hostfully.repository.BookingRepository;
import com.ampaiva.hostfully.repository.GuestRepository;
import com.ampaiva.hostfully.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BookingService extends BaseService<BookingDto, Booking> implements DtoService<BookingDto> {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final GuestRepository guestRepository;
    private final BlockRepository blockRepository;

    public BookingService(BookingMapper mapper, BookingRepository bookingRepository, PropertyRepository propertyRepository, GuestRepository guestRepository, BlockRepository blockRepository) {
        super(mapper, BookingDto.class, Booking.class, bookingRepository);
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.guestRepository = guestRepository;
        this.blockRepository = blockRepository;
    }


    @Override
    public BookingDto save(BookingDto dto) {
        Booking booking = toEntity(dto);
        checkValidDates(booking);
        Property property = propertyRepository.findById(booking.getProperty().getId()).orElseThrow();
        Guest guest = guestRepository.findById(booking.getGuest().getId()).orElseThrow();
        checkConflictingBookings(booking, property);
        checkConflictingBlock(booking, property);
        BookingDto savedDto = super.save(dto);
        Booking savedEntity = toEntity(savedDto);
        savedEntity.setProperty(property);
        savedEntity.setGuest(guest);
        return toDto(savedEntity);
    }

    private void checkValidDates(Booking booking) {
        if (booking.getStart().isAfter(booking.getEnd()))
            throw new PatchException("Start date (" + booking.getStart() + ") should be less than or equal to end date (" + booking.getEnd() + ")");
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
}
