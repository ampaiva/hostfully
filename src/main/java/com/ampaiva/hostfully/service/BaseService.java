package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.exception.PatchException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseService<T> {

    private final JpaRepository<T, Long> entityRepository;

    protected BaseService(JpaRepository<T, Long> entityRepository) {
        this.entityRepository = entityRepository;
    }

    public List<T> getAllEntities() {
        return entityRepository.findAll();
    }

    public Optional<T> getEntityById(Long id) {
        return entityRepository.findById(id);
    }

    public T saveEntity(T entity) {
        return entityRepository.save(entity);
    }

    public Optional<T> updateEntity(Long id, T updatedEntity) {
        Optional<T> existingEntity = entityRepository.findById(id);
        if (existingEntity.isPresent()) {
            BeanUtils.copyProperties(updatedEntity, existingEntity, "id");
            return Optional.of(entityRepository.save(existingEntity.get()));
        }


        return existingEntity;
    }

    public Optional<T> patchEntity(Long id, Map<String, Object> updates) {
        Optional<T> existingEntity = entityRepository.findById(id);
        return existingEntity.map(entity -> saveEntity(applyPatchWithExceptionHandling(entity, updates)));
    }

    protected T applyPatchWithExceptionHandling(T entity, Map<String, Object> updates) {
        try {
            entity = applyPatch(entity, updates);
        } catch (Exception e) {
            throw new PatchException("Error applying patch: " + e.getMessage());
        }
        return entity;
    }

    abstract protected T applyPatch(T entity, Map<String, Object> updates);

    @Transactional
    public Optional<T> deleteEntity(Long id) {
        Optional<T> existingEntity = entityRepository.findById(id);
        existingEntity.ifPresent(entityRepository::delete);


        return existingEntity;
    }

}
