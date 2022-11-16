package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
@Component
public class RatingDaoImpl implements RatingDao {
    private final JdbcTemplate jdbcTemplate;
    public RatingDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    @Override
    public Mpa getRatingById(Long id) {
        String sql="select * from motion_picture_association where mpa_id = ?";
       return jdbcTemplate.queryForObject(sql,(rs, rowNum) ->new Mpa(rs.getLong("mpa_id"),
               (rs.getString("mpa_name"))) ,id);
    }

    @Override
    public List<Mpa> getAllRating() {
        return jdbcTemplate.query("select * from motion_picture_association",(rs, rowNum) -> new Mpa(rs.getLong("mpa_id"),
                (rs.getString("mpa_name"))));
    }

}
