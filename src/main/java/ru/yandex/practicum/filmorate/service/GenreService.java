package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {
    private final GenreDaoImpl genreDao;

    public GenreService(GenreDaoImpl genreDao) {
        this.genreDao = genreDao;
    }
    public Genre getGenreById(Long id){
       if(id<0){
           throw new NotFoundException("Сущность не может быть найдена");
       }
        return genreDao.getGenreById(id);
    }
    public List<Genre>findAll(){
        return genreDao.getAllGenres();
    }
    public void deleteGenreById(Long id){
        genreDao.deleteGenreById(id);
    }

}
