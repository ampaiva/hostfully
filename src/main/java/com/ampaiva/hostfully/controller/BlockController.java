package com.ampaiva.hostfully.controller;

import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/block")
public class BlockController extends BaseController<Block> {
    @Autowired
    public BlockController(BlockService baseService) {
        super(baseService);
    }
}
