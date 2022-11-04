package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage  extends Storage<Film>{
    Film getFilmById(Long id);


}
