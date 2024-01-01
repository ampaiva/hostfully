package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.model.Property;
import com.ampaiva.hostfully.repository.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class PropertyService extends BaseService<Property> {

    public PropertyService(PropertyRepository entityRepository) {
        super(Property.class, entityRepository);
    }
}
