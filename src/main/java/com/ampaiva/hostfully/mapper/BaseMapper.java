package com.ampaiva.hostfully.mapper;

public interface BaseMapper<T, U> {
    T toDto(U entity);

    U toEntity(T dto);
}
