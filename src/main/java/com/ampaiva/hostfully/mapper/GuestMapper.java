package com.ampaiva.hostfully.mapper;

import com.ampaiva.hostfully.dto.GuestDto;
import com.ampaiva.hostfully.model.Guest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GuestMapper extends BaseMapper<GuestDto, Guest> {
}
