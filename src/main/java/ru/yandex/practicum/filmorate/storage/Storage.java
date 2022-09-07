package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {
    T create(T t);

    T update(T t);

    void delete(Long id);

    List<T> findAll();

}
