package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.exception.ConflictException;
import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.model.Booking;
import com.ampaiva.hostfully.model.Property;
import com.ampaiva.hostfully.repository.BlockRepository;
import com.ampaiva.hostfully.repository.BookingRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class ConflictCheckImpl implements ConflictCheck {
    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;

    public ConflictCheckImpl(BookingRepository bookingRepository, BlockRepository blockRepository) {
        this.bookingRepository = bookingRepository;
        this.blockRepository = blockRepository;
    }

    private void checkConflictingBookings(Long myId, Property property, Boolean canceled, LocalDate start, LocalDate end) {
        List<Booking> bookings = bookingRepository.findConflictingBookings(property, canceled, start, end);
        bookings.removeIf(b -> Objects.equals(b.getId(), myId));
        if (!bookings.isEmpty())
            throw new ConflictException("There are bookings for this property between " + start + " and " + end);
    }

    private void checkConflictingBooks(Long myId, Property property, LocalDate start, LocalDate end) {
        List<Block> blocks = blockRepository.findBlocksBy(property, start, end);
        blocks.removeIf(b -> Objects.equals(b.getId(), myId));
        if (!blocks.isEmpty())
            throw new ConflictException("There are blocks for this property between " + start + " and " + end);
    }

    @Override
    public void checkConflictingBookings(Booking booking) {
        checkConflictingBookings(booking.getId(), booking.getProperty(), booking.getCanceled(), booking.getStart(), booking.getEnd());
    }

    @Override
    public void checkConflictingBlocks(Booking booking) {
        checkConflictingBooks(booking.getId(), booking.getProperty(), booking.getStart(), booking.getEnd());
    }

    @Override
    public void checkConflictingBookings(Block block) {
        checkConflictingBookings(block.getId(), block.getProperty(), false, block.getStart(), block.getEnd());
    }

    @Override
    public void checkConflictingBlocks(Block block) {
        checkConflictingBooks(block.getId(), block.getProperty(), block.getStart(), block.getEnd());
    }
}
