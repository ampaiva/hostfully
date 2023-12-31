package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.repository.BlockRepository;
import com.ampaiva.hostfully.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class BlockService extends BaseService<Block> {
    private final PropertyRepository propertyRepository;

    public BlockService(BlockRepository entityRepository, PropertyRepository propertyRepository) {
        super(entityRepository);
        this.propertyRepository = propertyRepository;
    }


    public Block saveEntity(Block entity) {
        Block savedEntity = super.saveEntity(entity);
        savedEntity.setProperty(propertyRepository.findById(savedEntity.getProperty().getId()).orElseThrow());
        return savedEntity;
    }

    protected Block applyPatch(Block block, Map<String, Object> updates) {
        if (updates.containsKey("start")) {
            block.setStart(LocalDate.parse((String) updates.get("start")));
        }
        if (updates.containsKey("end")) {
            block.setEnd(LocalDate.parse((String) updates.get("end")));
        }
        return block;
    }
}
