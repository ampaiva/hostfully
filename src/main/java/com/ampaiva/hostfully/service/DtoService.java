package com.ampaiva.hostfully.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DtoService<T> {
    List<T> getAll();

    Optional<T> getById(Long id);

    T save(T entity);

    Optional<T> update(Long id, T updatedEntity);

    Optional<T> patch(Long id, Map<String, Object> updates);

    Optional<T> delete(Long id);
}
