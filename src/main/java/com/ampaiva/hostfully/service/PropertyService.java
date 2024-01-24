package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.dto.PropertyDto;
import com.ampaiva.hostfully.mapper.PropertyMapper;
import com.ampaiva.hostfully.model.Property;
import com.ampaiva.hostfully.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PropertyService extends BaseService<PropertyDto, Property> implements DtoService<PropertyDto> {

    @Autowired
    public PropertyService(PropertyMapper mapper, PropertyRepository entityRepository) {
        super(mapper, Property.class, entityRepository);
    }
}
