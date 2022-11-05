package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.LikeDaoImpl;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {
    private final LikeDaoImpl likeDao;
    private final FilmDaoImpl filmDao;
    private final Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmDaoImpl filmDao, LikeDaoImpl likeDao) {
        this.filmDao = filmDao;
        this.likeDao = likeDao;
    }

    public Film createFilm(Film film) {
        if (filmNotValid(film)) {
            throw new BadRequestException("Ошибка валидации при создании фильма");
        }
        return filmDao.create(film);
    }

    public Film updateFilm(Film film) {
        if (filmNotValid(film)) {
            throw new BadRequestException("Ошибка валидации при создании фильма");
        }
        if (filmDao.getById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм не может быть найден");
        }
        return filmDao.update(film);
    }

    public List<Film> findAllFilms() {
        return filmDao.findAll();
    }

    public Film getFilmById(Long id) {
        return filmDao.getById(id).orElseThrow(()->new NotFoundException("Фильм не может быть найден"));
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        likeDao.addLikeToFilm(filmId, userId);
    }

    public void deleteLike(Long userId, Long filmId) {
        if (filmId < 0 || userId < 0) {
            throw new NotFoundException("Сущности не могут быть найдены");
        }
        if (likeDao.getUsersWhichLikeFilm(filmId).contains(userId)) {
            likeDao.deleteLikeFromFilm(userId, filmId);
        } else {
            throw new ValidationException("Пользователь либо уже убрал лайк,либо еще и не лайкал");
        }
        filmDao.getById(filmId).get().getLikes().remove(userId);
    }

    public void delete(Long id) {
        filmDao.delete(id);
    }

    public List<Film> mostPopularFilms(Integer count) {
        return filmDao.getMostPopular(count);
    }

    public boolean filmNotValid(Film film) {
        if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза фильма должна быть не раньше  28 декабря 1985 года");
            return true;
        }
        return false;
    }

}