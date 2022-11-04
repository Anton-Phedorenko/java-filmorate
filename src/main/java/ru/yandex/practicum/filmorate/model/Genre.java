package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.constant.Genres;

import java.util.Objects;

public class Genre {
    private Long id;
    private Genres name;

    public Genre(Long id, Genres name) {
        this.id = id;
        this.name = name;
    }

    public Genre() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Genres getName() {
        return name;
    }

    public void setName(Genres name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", genre=" + name +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre1 = (Genre) o;
        return Objects.equals(id, genre1.id) && name == genre1.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
