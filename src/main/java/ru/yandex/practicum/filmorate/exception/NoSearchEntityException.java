package ru.yandex.practicum.filmorate.exception;

public class NoSearchEntityException extends RuntimeException{
    public NoSearchEntityException(String message){
        super(message);
    }
}
