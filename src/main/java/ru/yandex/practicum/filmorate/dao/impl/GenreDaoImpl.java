package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constant.Genres;
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
        String sql = "select * from GENRE where genre_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Genre(rs.getLong("genre_id"),
                Genres.valueOf(rs.getString("genre_name"))), genreId);

    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "select * from GENRE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getLong("genre_id"),
                Genres.valueOf(rs.getString("genre_name"))));
    }


    @Override
    public void deleteGenreById(Long genreId) {
        jdbcTemplate.update("delete from  film_genre where genre_id = ?", genreId);
    }

}
