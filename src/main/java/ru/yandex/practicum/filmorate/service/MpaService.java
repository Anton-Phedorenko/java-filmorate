package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.RatingDaoImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {
    private final RatingDaoImpl ratingDao;

    @Autowired
    public MpaService(RatingDaoImpl ratingDao) {
        this.ratingDao = ratingDao;
    }

    public List<Mpa> findAllMpa() {
        return ratingDao.getAllRating();
    }

    public Mpa getMpaById(Long mpaId) {
        if (mpaId < 0) {
            throw new NotFoundException("Сущность не может быть найдена");
        }
        return ratingDao.getRatingById(mpaId);
    }

}
