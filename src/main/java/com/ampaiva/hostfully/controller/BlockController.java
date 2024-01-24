package com.ampaiva.hostfully.controller;

import com.ampaiva.hostfully.dto.BlockDto;
import com.ampaiva.hostfully.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blocks")
public class BlockController extends BaseController<BlockDto> {
    @Autowired
    public BlockController(BlockService baseService) {
        super(baseService);
    }
}
