package com.ampaiva.hostfully.mapper;

import com.ampaiva.hostfully.dto.BlockDto;
import com.ampaiva.hostfully.model.Block;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlockMapper extends BaseMapper<BlockDto, Block> {
}
