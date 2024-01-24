package com.ampaiva.hostfully.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DtoService<T> {
    public List<T> getAll();

    public Optional<T> getById(Long id);

    public T save(T entity);

    public Optional<T> update(Long id, T updatedEntity);

    public Optional<T> patch(Long id, Map<String, Object> updates);

    public Optional<T> delete(Long id);
}
