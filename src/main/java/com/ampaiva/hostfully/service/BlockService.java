package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.dto.BlockDto;
import com.ampaiva.hostfully.exception.BadRequestException;
import com.ampaiva.hostfully.mapper.BlockMapper;
import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.repository.BlockRepository;
import com.ampaiva.hostfully.repository.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class BlockService extends BaseService<BlockDto, Block> implements DtoService<BlockDto> {
    private final PropertyRepository propertyRepository;

    public BlockService(BlockMapper mapper, BlockRepository entityRepository, PropertyRepository propertyRepository) {
        super(mapper, Block.class, entityRepository);
        this.propertyRepository = propertyRepository;
    }

    @Override
    public BlockDto save(BlockDto dto) {
        BlockDto savedDto = super.save(dto);
        Block block = toEntity(savedDto);
        checkValidDates(block);
        block.setProperty(propertyRepository.findById(block.getProperty().getId()).orElseThrow());
        return toDto(block);
    }

    private void checkValidDates(Block block) {
        if (block.getStart().isAfter(block.getEnd()))
            throw new BadRequestException("Start date (" + block.getStart() + ") should be less than or equal to end date (" + block.getEnd() + ")");
    }
}
