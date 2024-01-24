package com.ampaiva.hostfully.mapper;

import com.ampaiva.hostfully.dto.GuestDto;
import com.ampaiva.hostfully.model.Guest;
import org.mapstruct.Mapper;

@Mapper
public interface GuestMapper extends BaseMapper<GuestDto, Guest> {
}
