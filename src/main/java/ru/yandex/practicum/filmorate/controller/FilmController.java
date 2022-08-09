package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    Logger log=LoggerFactory.getLogger(FilmController.class);
    Map<Integer, Film> films = new HashMap<>();
    private int filmId;
    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film){
        if(validateFilm(film)){
            film.setId(generateId());
            films.put(film.getId(),film);
            log.debug("Фильм "+film.getName()+" успешно добавлен");
            return film;
        }
        else{
            throw new ValidationException("Ошибка валидации при попытке добавить фильм");
        }
    }
    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film){
        if(validateFilm(film)&&films.containsKey(film.getId())){
            films.put(film.getId(),film);
            log.debug("Фильм обновлен");
            return film;
        }
        else {
            throw new ValidationException("Ошибка при попытке обновить фильм");
        }
    }
    public boolean validateFilm(Film film){
//        if(!film.getName().isEmpty()&&film.getDescription().length()<200&&
//                film.getReleaseDate().isAfter(LocalDate.of(1895,12,28))&&film.getDuration()>0){
//            return true;
//        }
//        return false;
        if(film.getName().isEmpty()){
            throw new ValidationException("У фильма должно быть название");
        }
        else if(film.getDescription().length()>200){
            throw new ValidationException("Слишком длинное описание фильма");
        }
        else if(!film.getReleaseDate().isAfter(LocalDate.of(1895,12,28))){
            throw new ValidationException("В то время вряд ли могли выпустить фльм:(");
        }
        else if(film.getDuration()<0){
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }
        return true;
    }
    private int generateId(){
        this.filmId++;
        return this.filmId;
    }
}
