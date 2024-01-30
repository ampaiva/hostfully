package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.dto.BookingDto;
import com.ampaiva.hostfully.exception.BadRequestException;
import com.ampaiva.hostfully.exception.ConflictException;
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

    private final PropertyRepository propertyRepository;
    private final GuestRepository guestRepository;
    private final ConflictCheck conflictCheck;

    public BookingService(BookingMapper mapper, BookingRepository bookingRepository,
                          PropertyRepository propertyRepository, GuestRepository guestRepository,
                          ConflictCheck conflictCheck) {
        super(mapper, Booking.class, bookingRepository);
        this.propertyRepository = propertyRepository;
        this.guestRepository = guestRepository;
        this.conflictCheck = conflictCheck;
    }


    @Override
    public BookingDto save(BookingDto dto) {
        Booking booking = toEntity(dto);

        validate(booking);

        Property property = propertyRepository.findById(booking.getProperty().getId()).orElseThrow();
        Guest guest = guestRepository.findById(booking.getGuest().getId()).orElseThrow();

        BookingDto savedDto = super.save(dto);
        Booking savedEntity = toEntity(savedDto);
        savedEntity.setProperty(property);
        savedEntity.setGuest(guest);
        return toDto(savedEntity);
    }

    private void validate(Booking booking) {
        checkValidDates(booking);
        checkConflicts(booking);
    }

    private void checkConflicts(Booking booking) {
        conflictCheck.checkConflictingBookings(booking);
        conflictCheck.checkConflictingBlocks(booking);
    }

    private void checkValidDates(Booking booking) {
        if (booking.getStart().isAfter(booking.getEnd()))
            throw new BadRequestException("Start date (" + booking.getStart() + ") should be less than or equal to end date (" + booking.getEnd() + ")");
    }
}
