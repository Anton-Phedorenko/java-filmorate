package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikeDao;

import java.util.List;

@Component
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;

    public LikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeToFilm(Long filmId, Long userId) {
        jdbcTemplate.update("INSERT INTO film_user (film_id,user_id) VALUES (?,?)", filmId, userId);
        updateFilmLikes(filmId);
    }

    @Override
    public void deleteLikeFromFilm(Long userId, Long filmId) {
        jdbcTemplate.update("DELETE FROM film_user WHERE film_id=? AND user_id=?", filmId, userId);
        updateFilmLikes(filmId);
    }

    public List<Long> getUsersWhichLikeFilm(Long filmId) {
        String sql =
                "SELECT fu.USER_ID " +
                        "FROM FILM F " +
                        "JOIN film_user fu  ON F.FILM_ID = fu.FILM_ID " +
                        "WHERE F.FILM_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    public void updateFilmLikes(Long filmId) {
        String sql = "UPDATE film f SET rate=(SELECT COUNT(fu.user_id) FROM film_user fu WHERE fu.film_id=f.film_id)" +
                " WHERE film_id=?";
        jdbcTemplate.update(sql, filmId);
    }

}
