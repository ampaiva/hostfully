package com.ampaiva.hostfully.repository;

import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("SELECT e FROM Block e WHERE e.property = :property and ((e.start <= :start AND e.end >= :start) OR (e.start <= :end AND e.end >= :end))")
    List<Block> findBlocksBy(Property property, LocalDate start, LocalDate end);
}
