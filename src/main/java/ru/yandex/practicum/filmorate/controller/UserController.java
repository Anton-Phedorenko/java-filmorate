package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private int userId;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> userAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        if (userNotValid(user)) {
            throw new BadRequestException("Ошибка при создании пользователя");
        }

        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("Пользователь " + user.getName() + " успешно добавлен");

        return user;

    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {
        if (userNotValid(user)) {
            throw new BadRequestException("Ошибка при обновлении пользователя");

        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Сущность не найдена");
        }
        users.put(user.getId(), user);
        log.debug("Пользователь успешно обновлен");
        return user;
    }

    private boolean userNotValid(User user) {
        if (!user.getBirthday().isBefore(LocalDate.now())) {
            log.debug("Пользователь не мог родиться в будущем ¯\\_(ツ)_/¯ ");
            return true;
        }

        return false;
    }

    private int generateId() {
        return ++this.userId;
    }

}
