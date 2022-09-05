package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Long userId = Long.valueOf(0);
    private Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private HashMap<Long, User> users = new HashMap<>();


    @Override
    public User create(User user) {
        if (userNotValid(user)) {
            throw new BadRequestException("Ошибка при созздании пользователя");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("Пользователь успешно создан");
        return user;
    }

    @Override
    public User update(User user) {
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

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь,которого вы хотите удалить,не существует");
        }
        users.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            log.debug("Пользователь с id " + id + " не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    private boolean userNotValid(User user) {
        if (!user.getBirthday().isBefore(LocalDate.now())) {
            log.debug("Пользователь не мог родиться в будущем ¯\\_(ツ)_/¯ ");
            return true;
        }
        return false;
    }

    private Long generateId() {
        return ++this.userId;
    }

}
