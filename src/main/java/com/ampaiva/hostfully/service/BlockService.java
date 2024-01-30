package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.dto.BlockDto;
import com.ampaiva.hostfully.exception.BadRequestException;
import com.ampaiva.hostfully.mapper.BlockMapper;
import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.model.Property;
import com.ampaiva.hostfully.repository.BlockRepository;
import com.ampaiva.hostfully.repository.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class BlockService extends BaseService<BlockDto, Block> implements DtoService<BlockDto> {
    private final PropertyRepository propertyRepository;
    private final ConflictCheck conflictCheck;

    public BlockService(BlockMapper mapper, BlockRepository entityRepository, PropertyRepository propertyRepository,
                        ConflictCheck conflictCheck) {
        super(mapper, Block.class, entityRepository);
        this.propertyRepository = propertyRepository;
        this.conflictCheck = conflictCheck;
    }

    @Override
    public BlockDto save(BlockDto dto) {

        Block block = toEntity(dto);

        validate(block);

        Property property = propertyRepository.findById(block.getProperty().getId()).orElseThrow();

        BlockDto savedDto = super.save(dto);
        Block savedEntity = toEntity(savedDto);
        savedEntity.setProperty(property);
        return toDto(savedEntity);
    }

    private void validate(Block block) {
        checkValidDates(block);
        checkConflicts(block);
    }

    private void checkConflicts(Block block) {
        conflictCheck.checkConflictingBookings(block);
        conflictCheck.checkConflictingBlocks(block);
    }

    private void checkValidDates(Block block) {
        if (block.getStart().isAfter(block.getEnd()))
            throw new BadRequestException("Start date (" + block.getStart() + ") should be less than or equal to end date (" + block.getEnd() + ")");
    }
}
