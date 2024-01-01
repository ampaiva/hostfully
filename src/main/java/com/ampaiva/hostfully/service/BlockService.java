package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.repository.BlockRepository;
import com.ampaiva.hostfully.repository.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class BlockService extends BaseService<Block> {
    private final PropertyRepository propertyRepository;

    public BlockService(BlockRepository entityRepository, PropertyRepository propertyRepository) {
        super(Block.class, entityRepository);
        this.propertyRepository = propertyRepository;
    }

    public Block saveEntity(Block entity) {
        Block savedEntity = super.saveEntity(entity);
        savedEntity.setProperty(propertyRepository.findById(savedEntity.getProperty().getId()).orElseThrow());
        return savedEntity;
    }
}
