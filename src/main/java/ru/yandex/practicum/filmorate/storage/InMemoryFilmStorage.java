package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private HashMap<Long, Film> films = new HashMap<>();
    private Long filmId= Long.valueOf(0);

    @Override
    public Film createFilm(Film film) {
        if (filmNotValid(film)) {
            throw new BadRequestException("Ошибка валидации при попытке добавить фильм");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        log.debug("Фильм " + film.getName() + " успешно добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmNotValid(film)) {
            throw new BadRequestException("Ошибка при попытке обновить фильм");
        }
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Сущность не найдена");
        }
        films.put(film.getId(), film);
        log.debug("Фильм обновлен");
        return film;
    }

    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
        return films.get(id);
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    private Long generateId() {
        return ++this.filmId;
    }

    public boolean filmNotValid(Film film) {
        if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза фильма должна быть не раньше  28 декабря 1985 года");
            return true;
        }

        return false;

    }

    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            log.debug("Фильм с id " + id + " не найден");
            return films.get(id);
        }
        else {
        throw new FilmNotFoundException("Фильм не найден");
    }

    }

}
