package ru.practicum.shareit.util;

import java.util.Collection;
import java.util.Optional;

public interface Storage<T> {
    T create(T object);

    Optional<T> get(Long id);

    T update(T object);

    void delete(Long id);

    Collection<T> getAll();
}

