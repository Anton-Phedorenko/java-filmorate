package ru.yandex.practicum.filmorate.dao.storage;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {
    T create(T t);

    T update(T t);

    void delete(Long id);

    List<T> findAll();
    Optional<T> getById(Long id);
}
