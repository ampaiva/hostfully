package com.ampaiva.hostfully.controller;

import com.ampaiva.hostfully.model.Guest;
import com.ampaiva.hostfully.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest")
public class GuestController extends BaseController<Guest> {
    @Autowired
    public GuestController(GuestService baseService) {
        super(baseService);
    }
}
