package com.ampaiva.hostfully.config;

import com.ampaiva.hostfully.mapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.ampaiva.hostfully.mapper")
public class MapStructConfig {

    @Bean
    public BlockMapper blockMapper() {
        return new BlockMapperImpl();
    }

    @Bean
    public BookingMapper bookingMapper() {
        return new BookingMapperImpl();
    }

    @Bean
    public GuestMapper guestMapper() {
        return new GuestMapperImpl();
    }

    @Bean
    public PropertyMapper propertyMapper() {
        return new PropertyMapperImpl();
    }
}
