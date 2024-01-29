package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.BlockDto;


public class BlockIntegrationTest extends BaseIntegrationTest {

    BlockIntegrationTest() {
        super(BlockDto.class, "blocks", "block");
    }

    @Override
    public void testGetNonExisting() {

    }

    @Override
    public void testCreateWithMissingNonNullableFields() {

    }
}
