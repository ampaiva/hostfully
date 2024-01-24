package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.dto.GuestDto;
import com.ampaiva.hostfully.mapper.GuestMapper;
import com.ampaiva.hostfully.model.Guest;
import com.ampaiva.hostfully.repository.GuestRepository;
import org.springframework.stereotype.Service;

@Service
public class GuestService extends BaseService<GuestDto, Guest> implements DtoService<GuestDto> {

    public GuestService(GuestMapper mapper, GuestRepository entityRepository) {
        super(mapper, GuestDto.class, Guest.class, entityRepository);
    }
}
