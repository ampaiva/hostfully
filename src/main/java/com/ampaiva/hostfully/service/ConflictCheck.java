package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.model.Booking;

public interface ConflictCheck {
    void checkConflictingBookings(Booking booking);
    void checkConflictingBlocks(Booking booking);
    void checkConflictingBookings(Block block);
    void checkConflictingBlocks(Block block);
}
