package com.ampaiva.hostfully.controller;

import com.ampaiva.hostfully.service.BaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseController<T> {
    private final BaseService<T> baseService;

    public BaseController(BaseService<T> baseService) {
        this.baseService = baseService;
    }

    @GetMapping
    public ResponseEntity<List<T>> getAllEntities() {
        return new ResponseEntity<>(baseService.getAllEntities(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getEntityById(@PathVariable Long id) {
        Optional<T> optionalEntity = baseService.getEntityById(id);
        return optionalEntity.map(entity -> new ResponseEntity<>(entity, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<T> saveEntity(@RequestBody T entity) {
        return new ResponseEntity<>(baseService.saveEntity(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntity(@PathVariable Long id, @RequestBody T updatedEntity) {
        Optional<T> existingEntity = baseService.updateEntity(id, updatedEntity);
        if (existingEntity.isEmpty()) {
            return new ResponseEntity<>("id not found: " + id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(existingEntity, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchEntity(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<T> existingEntity = baseService.patchEntity(id, updates);

        if (existingEntity.isEmpty()) {
            return new ResponseEntity<>("id not found: " + id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(existingEntity.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntity(@PathVariable Long id) {
        Optional<T> deletedEntity = baseService.deleteEntity(id);
        return deletedEntity.map(t -> new ResponseEntity<>(t.getClass().getSimpleName() + " " + id + " deleted successfully", HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>("id not found: " + id, HttpStatus.NOT_FOUND));

    }
}
