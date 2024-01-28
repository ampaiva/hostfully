package com.ampaiva.hostfully.controller;

import com.ampaiva.hostfully.service.DtoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseController<T> {
    private final DtoService<T> dtoService;

    public BaseController(DtoService<T> dtoService) {
        this.dtoService = dtoService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<T>> getAll() {
        return new ResponseEntity<>(dtoService.getAll(), HttpStatus.OK);
    }

    @Operation(summary = "Get an entity by its id")
    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable Long id) {
        Optional<T> optionalEntity = dtoService.getById(id);
        return optionalEntity.map(entity -> new ResponseEntity<>(entity, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<T> save(@RequestBody T dto) {
        return new ResponseEntity<>(dtoService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody T updatedEntity) {
        Optional<T> optionalDto = dtoService.update(id, updatedEntity);
        if (optionalDto.isEmpty()) {
            return new ResponseEntity<>("id not found: " + id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<T> optionalDto = dtoService.patch(id, updates);

        if (optionalDto.isEmpty()) {
            return new ResponseEntity<>("id not found: " + id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalDto.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<T> deletedDto = dtoService.delete(id);
        return deletedDto.map(t -> new ResponseEntity<>(HttpStatus.NO_CONTENT)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
}
