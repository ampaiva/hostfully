package com.ampaiva.hostfully.controller;

import com.ampaiva.hostfully.dto.BookingDto;
import com.ampaiva.hostfully.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController extends BaseController<BookingDto> {
    @Autowired
    public BookingController(BookingService baseService) {
        super(baseService);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        return updateCanceled(id, true);
    }

    @PatchMapping("/{id}/rebook")
    public ResponseEntity<?> rebook(@PathVariable Long id) {
        return updateCanceled(id, false);
    }

    private ResponseEntity<?> updateCanceled(Long id, boolean canceled) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("canceled", canceled);
        return patch(id, updates);
    }
}
