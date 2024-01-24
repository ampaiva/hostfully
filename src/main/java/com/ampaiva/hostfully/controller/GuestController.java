package com.ampaiva.hostfully.controller;

import com.ampaiva.hostfully.dto.GuestDto;
import com.ampaiva.hostfully.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guests")
public class GuestController extends BaseController<GuestDto> {
    @Autowired
    public GuestController(GuestService baseService) {
        super(baseService);
    }
}
