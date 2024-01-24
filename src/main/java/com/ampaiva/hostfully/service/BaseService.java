package com.ampaiva.hostfully.service;

import com.ampaiva.hostfully.exception.PatchException;
import com.ampaiva.hostfully.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseService<T, U> {

    private final BaseMapper<T, U> mapper;
    private final Class<U> entityClass;

    private final JpaRepository<U, Long> entityRepository;

    protected BaseService(BaseMapper<T, U> mapper, Class<U> entityClass, JpaRepository<U, Long> entityRepository) {
        this.mapper = mapper;
        this.entityClass = entityClass;
        this.entityRepository = entityRepository;
    }

    private static Object getConvertedValue(Field field, Object fieldValue) {
        if (field.getType() == LocalDate.class && fieldValue != null)
            fieldValue = LocalDate.parse(fieldValue.toString());
        return fieldValue;
    }

    public List<T> getAll() {
        return entityRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<T> getById(Long id) {
        return entityRepository.findById(id).map(this::toDto);
    }

    public T save(T dto) {
        return toDto(entityRepository.save(toEntity(dto)));
    }

    public Optional<T> update(Long id, T updatedDto) {
        Optional<U> existingEntity = entityRepository.findById(id);
        if (existingEntity.isPresent()) {
            U updatedEntity = toEntity(updatedDto);
            BeanUtils.copyProperties(updatedEntity, existingEntity, "id");
            return Optional.of(entityRepository.save(applyUpdate(existingEntity.get(), updatedEntity))).map(this::toDto);
        }

        return existingEntity.map(this::toDto);
    }

    public Optional<T> patch(Long id, Map<String, Object> updates) {
        Optional<U> existingEntity = entityRepository.findById(id);
        return existingEntity.map(entity -> save(toDto(applyPatchWithExceptionHandling(entity, updates))));
    }

    protected U applyPatchWithExceptionHandling(U entity, Map<String, Object> updates) {
        try {
            entity = applyPatch(entity, updates);
        } catch (Exception e) {
            throw new PatchException("Error applying patch: " + e.getMessage());
        }
        return entity;
    }

    protected U applyUpdate(U entity, U updatedEntity) {
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

    protected U applyPatch(U entity, Map<String, Object> updates) {
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            try {
                Field field = entityClass.getDeclaredField(fieldName);

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
    public Optional<T> delete(Long id) {
        Optional<U> existingEntity = entityRepository.findById(id);
        existingEntity.ifPresent(entityRepository::delete);


        return existingEntity.map(this::toDto);
    }

    public T toDto(U entity) {
        return mapper.toDto(entity);
    }

    public U toEntity(T dto) {
        return mapper.toEntity(dto);
    }
}
