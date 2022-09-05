package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        log.info("Добавление фильма");
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Обновление данных о фильме");
        return filmService.updateFilm(film);
    }


    @GetMapping
    public List<Film> getFilms() {
        log.info("Показ всех фильмов");
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Long id) {
        log.info("Показ фильма по его id");
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable("userId") Long userId,
                              @PathVariable("id") Long filmId) {
        log.info("Пользователь с id = " + userId + " поставил лайк фильму с id = " + filmId);
        filmService.addLikeToFilm(userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToFilm(@PathVariable("userId") Long userId,
                                 @PathVariable("id") Long filmId) {
        log.info("Пользователь с id = " + userId + " убрал лайк с фильма с id = " + filmId);
        filmService.deleteLike(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> getTopTenFilms(@Positive @RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Топ " + count + " лучших фильмов");
        return filmService.mostPopularFilms(count);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Удаление фильма по id");
        filmService.delete(id);
    }

}
