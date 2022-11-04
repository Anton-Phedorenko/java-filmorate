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
        jdbcTemplate.update("insert into film_user (film_id,user_id) values (?,?)", filmId, userId);
        updateFilmLikes(filmId);
    }

    @Override
    public void deleteLikeFromFilm(Long userId, Long filmId) {
        jdbcTemplate.update("delete from film_user where film_id=? and user_id=?", filmId, userId);
        updateFilmLikes(filmId);
    }

    public List<Long> getUsersWhichLikeFilm(Long filmId) {
        String sql =
                "select fu.USER_ID " +
                        "from FILM F " +
                        "join film_user fu  on F.FILM_ID = fu.FILM_ID " +
                        "where F.FILM_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    public void updateFilmLikes(Long filmId) {
        String sql = "update film f set rate=(select count(fu.user_id) from film_user fu where fu.film_id=f.film_id)" +
                " where film_id=?";
        jdbcTemplate.update(sql, filmId);
    }

}
