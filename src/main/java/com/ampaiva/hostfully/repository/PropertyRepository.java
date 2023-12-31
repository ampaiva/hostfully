package com.ampaiva.hostfully.repository;

import com.ampaiva.hostfully.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
