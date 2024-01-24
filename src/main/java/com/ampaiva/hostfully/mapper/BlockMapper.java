package com.ampaiva.hostfully.mapper;

import com.ampaiva.hostfully.dto.BlockDto;
import com.ampaiva.hostfully.model.Block;
import org.mapstruct.Mapper;

@Mapper
public interface BlockMapper extends BaseMapper<BlockDto, Block> {
}
