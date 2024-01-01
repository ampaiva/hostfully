package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.exception.PatchException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseService<T> {

    private final Class<T> type;

    private final JpaRepository<T, Long> entityRepository;

    protected BaseService(Class<T> type, JpaRepository<T, Long> entityRepository) {
        this.type = type;
        this.entityRepository = entityRepository;
    }

    private static Object getConvertedValue(Field field, Object fieldValue) {
        if (field.getType() == LocalDate.class && fieldValue != null)
            fieldValue = LocalDate.parse(fieldValue.toString());
        return fieldValue;
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
            return Optional.of(entityRepository.save(applyUpdate(existingEntity.get(), updatedEntity)));
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

    protected T applyUpdate(T entity, T updatedEntity) {
        Map<String, Object> updates = new HashMap<>();
        for (Field field : updatedEntity.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(updatedEntity);
                if (value != null)
                    updates.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                throw new PatchException("Error applying patch: " + e.getMessage());
            }
        }
        return applyPatch(entity, updates);
    }

    protected T applyPatch(T entity, Map<String, Object> updates) {
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            try {
                Field field = type.getDeclaredField(fieldName);

                field.setAccessible(true);

                fieldValue = getConvertedValue(field, fieldValue);

                field.set(entity, fieldValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new PatchException("Error applying patch: " + e.getMessage());
            }
        }
        return entity;
    }

    @Transactional
    public Optional<T> deleteEntity(Long id) {
        Optional<T> existingEntity = entityRepository.findById(id);
        existingEntity.ifPresent(entityRepository::delete);


        return existingEntity;
    }

}
