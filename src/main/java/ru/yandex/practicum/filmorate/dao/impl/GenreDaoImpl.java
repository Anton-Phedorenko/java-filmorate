package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Long genreId) {
        String sql = "SELECT * FROM GENRE WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")), genreId);

    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
    }


    @Override
    public void deleteGenreById(Long genreId) {
        jdbcTemplate.update("DELETE FROM  film_genre WHERE genre_id = ?", genreId);
    }

}
