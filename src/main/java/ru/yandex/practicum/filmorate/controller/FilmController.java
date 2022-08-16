package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    Map<Integer, Film> films = new HashMap<>();
    private int filmId;

    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        if (filmNotValid(film)) {
            throw new BadRequestException("Ошибка валидации при попытке добавить фильм");
        }

        film.setId(generateId());
        films.put(film.getId(), film);
        log.debug("Фильм " + film.getName() + " успешно добавлен");

        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (filmNotValid(film)) {
            throw new BadRequestException("Ошибка при попытке обновить фильм");
        }

        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Сущность не найдена");
        }
        films.put(film.getId(), film);
        log.debug("Фильм обновлен");

        return film;
    }

    public boolean filmNotValid(Film film) {
        if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза фильма должна быть не раньше  28 декабря 1985 года");
            return true;
        }

        return false;

    }

    private int generateId() {
        return ++this.filmId;
    }

}
