package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }
    public Film createFilm(Film film){
        return filmStorage.createFilm(film);
    }
    public Film updateFilm(Film film){
       return filmStorage.updateFilm(film);
    }
    public List<Film>findAllFilms(){
        return filmStorage.findAll();
    }
    public Film getFilmById(Long id){
        return filmStorage.getFilmById(id);
    }
    public void addLikeToFilm(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId).getLikes().add(userId);
    }
    public void deleteLike(Long filmId,Long userId){
        filmStorage.getFilmById(filmId).getLikes().remove(userId);
    }
    public List<Film>mostPopularFilms(Integer count){
        if(count==null){
            return filmStorage.findAll().stream().sorted((f1,f2)->f1.getLikes().size()-f2.getLikes().size()).limit(10).
                    collect(Collectors.toList());
        }
        return filmStorage.findAll().stream().sorted((f1,f2)->f1.getLikes().size()-f2.getLikes().size()).limit(count).
                collect(Collectors.toList());
    }

}
