package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.model.Guest;
import com.ampaiva.hostfully.repository.GuestRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GuestService extends BaseService<Guest> {

    public GuestService(GuestRepository entityRepository) {
        super(Guest.class, entityRepository);
    }
}
