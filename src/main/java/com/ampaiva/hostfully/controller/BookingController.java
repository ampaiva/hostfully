package com.ampaiva.hostfully.controller;

import com.ampaiva.hostfully.model.Booking;
import com.ampaiva.hostfully.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/booking")
public class BookingController extends BaseController<Booking> {
    @Autowired
    public BookingController(BookingService baseService) {
        super(baseService);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        return updateCanceled(id, true);
    }
    @PatchMapping("/rebook/{id}")
    public ResponseEntity<?> rebook(@PathVariable Long id) {
        return updateCanceled(id, false);
    }

    private ResponseEntity<?> updateCanceled(Long id, boolean canceled) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("canceled", canceled);
        return patchEntity(id, updates);
    }
}
