package com.ampaiva.hostfully.mapper;

import com.ampaiva.hostfully.dto.BookingDto;
import com.ampaiva.hostfully.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper extends BaseMapper<BookingDto, Booking> {
}
