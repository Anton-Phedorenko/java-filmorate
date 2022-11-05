package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FilmDaoImpl implements FilmStorage {
    private final RatingDaoImpl ratingDao;
    private final GenreDaoImpl genreDao;
    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate, RatingDaoImpl ratingDao, GenreDaoImpl genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingDao = ratingDao;
        this.genreDao = genreDao;
    }


    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO film (title,description,release_date,duration,rating_mpa)" +
                "VALUES(?,?,?,?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;

        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        film.setId(id);
        Mpa mpa = ratingDao.getRatingById(film.getMpa().getId());
        film.setMpa(mpa);
        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres().
                    stream().
                    map(genre -> genre.getId()).
                    map(i -> genreDao.getGenreById(i)).
                    collect(Collectors.toList());
            film.setGenres(genres);
            insertGenres(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE film SET " +
                "film_id=?," +
                "title = ?," +
                "description = ?," +
                "release_date = ?," +
                "duration = ?," +
                "rating_mpa = ?" +
                " WHERE film_id= ?";
        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (getFilmGenresByFilmId(film.getId())) {
            String delGenreBeforeUpdate = "DELETE FROM film_genre WHERE film_id = ?";
            jdbcTemplate.update(delGenreBeforeUpdate, film.getId());
            insertGenres(film);
        } else {
            insertGenres(film);
        }

        return getById(film.getId()).get();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM film";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        return films;
    }

    @Override
    public Optional<Film> getById(Long id) {
        String sql = "SELECT * FROM film WHERE film_id = ?";
        Optional<Film> film = Optional.ofNullable(jdbcTemplate.query(sql, rs -> rs.next() ? makeFilm(rs) : null, id));
        return film;
    }

    public List<Film> getMostPopular(Integer count) {
        String sql = "SELECT * FROM film ORDER BY rate DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs)).
                stream().
                limit(count).
                collect(Collectors.toList());
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("title"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        Integer id = rs.getInt("rating_mpa");
        if (id > 0) {
            Mpa mpa = ratingDao.getRatingById(rs.getLong("rating_mpa"));
            film.setMpa(mpa);
        }
        String sql1 = "SELECT g.genre_id,genre_name FROM genre g JOIN film_genre fg ON fg.genre_id = g.genre_id" +
                " WHERE film_id=?";
        List<Genre> genres = jdbcTemplate.query(sql1, (rs1, rowNum) -> new Genre(rs1.getLong("genre_id"),
                rs1.getString("genre_name")), film.getId());
        if (genres.size() != 0) {
            film.setGenres(genres);
        } else {
            film.setGenres(Collections.emptyList());
        }
        return film;
    }

    private void insertGenres(Film film) {
        String sqlForGenre = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sqlForGenre, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                Genre genre = film.getGenres().get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                if (film.getGenres() != null) {
                    return new HashSet<>(film.getGenres()).size();
                }
                return 0;
            }
        });
    }

    private Boolean getFilmGenresByFilmId(Long filmId) {
        String sql = "SELECT film_id FROM film_genre WHERE film_id = " + filmId;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id")).contains(filmId);
    }

}