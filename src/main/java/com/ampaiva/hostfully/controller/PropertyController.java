package com.ampaiva.hostfully.controller;

import com.ampaiva.hostfully.dto.PropertyDto;
import com.ampaiva.hostfully.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/properties")
public class PropertyController extends BaseController<PropertyDto> {
    @Autowired
    public PropertyController(PropertyService baseService) {
        super(baseService);
    }
}
