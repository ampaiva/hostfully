package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.model.Guest;
import com.ampaiva.hostfully.repository.GuestRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GuestService extends BaseService<Guest> {

    public GuestService(GuestRepository entityRepository) {
        super(entityRepository);
    }

    protected Guest applyPatch(Guest Guest, Map<String, Object> updates) {
        return Guest;
    }
}
