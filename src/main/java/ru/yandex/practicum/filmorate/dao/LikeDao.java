package ru.yandex.practicum.filmorate.dao;

public interface LikeDao {
    void addLikeToFilm(Long filmId,Long userId);
    void deleteLikeFromFilm(Long filmId,Long userId);
}
