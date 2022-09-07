package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private HashMap<Long, Film> films = new HashMap<>();
    private Long filmId = Long.valueOf(0);

    @Override
    public Film create(Film film) {
        if (filmNotValid(film)) {
            throw new BadRequestException("Ошибка валидации при попытке добавить фильм");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        log.debug("Фильм {} успешно добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (filmNotValid(film)) {
            throw new BadRequestException("Ошибка при попытке обновить фильм");
        }
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Сущность не найдена");
        }
        films.put(film.getId(), film);
        log.debug("Фильм {} обновлен", film.getName());
        return film;
    }

    @Override
    public void delete(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм не найден");
        }
        films.remove(id);
        log.debug("Фильм c id {} удален", id);
    }

    @Override
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

    @Override
    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.debug("Фильм с id {} не найден", id);
            throw new NotFoundException("Фильм не найден");
        }

    }

}
