package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.*;

@RestController
public class UserController {
    Logger log=LoggerFactory.getLogger(UserController.class);
    private int userId;
    private Map<Integer, User> users = new HashMap<>();
    @GetMapping("/users")
    public List<User>userAll(){
        return new ArrayList<>(users.values());
    }
    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user){
        if(validateUser(user)){
            user.setId(generateId());
            users.put(user.getId(),user);
            log.debug("Пользователь "+ user.getName()+" успешно добавлен");
            return user;
        }
        else {
            throw new ValidationException("Ошибка при создании пользователя");
        }
    }
    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
            if (validateUser(user)&&users.containsKey(user.getId())) {
                users.put(user.getId(),user);
                log.debug("Пользователь успешно обновлен");
                return user;
            }
         else {
            throw new ValidationException("Ошибка при обновлении пользователя");
        }
    }
    private boolean validateUser(User user){
        if(user.getEmail().isEmpty()&&!user.getEmail().contains("@")){
            throw new ValidationException("Проверьте email пользователя");
        }
        else if(user.getLogin().isEmpty()||user.getLogin().contains(" ")){
            throw new ValidationException("Некорректный логин");
        }
        else if (!user.getBirthday().isBefore(LocalDate.now())){
            throw new ValidationException("Пользователь не мог родиться в будущем ¯\\_(ツ)_/¯ ");
        }
        if (user.getName().isEmpty()){
            user.setName(user.getLogin());
            return true;
        }
        return true;
    }
    private int generateId(){
        this.userId++;
        return this.userId;
    }
}
