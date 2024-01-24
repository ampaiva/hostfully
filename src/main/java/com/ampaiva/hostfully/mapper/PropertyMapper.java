package com.ampaiva.hostfully.mapper;

import com.ampaiva.hostfully.dto.PropertyDto;
import com.ampaiva.hostfully.model.Property;
import org.mapstruct.Mapper;

@Mapper
public interface PropertyMapper extends BaseMapper<PropertyDto, Property> {
}
