package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    private final UserDaoImpl userDao;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    public User createUser(User user) {
        if (userNotValid(user)) {
            throw new BadRequestException("Ошибка валидации при создании пользователя");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new BadRequestException("Пользователь не мог родиться в будущем");
        }
        return userDao.create(user);
    }

    public User updateUser(User user) {
        if (userNotValid(user)) {
            throw new BadRequestException("Ошибка валидации при обновлении пользователя");
        }
        if (userDao.getById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь не может быть найден");
        }
        return userDao.update(user);
    }

    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    public User getUserById(Long id) {

        return userDao.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не может быть найден"));
    }

    public boolean addFriend(Long userId, Long friendId) {
        if (userId < 0 || friendId < 0) {
            throw new NotFoundException("Id не может быть отрицательным");
        }
        userDao.addFriend(userId, friendId);
        return false;
    }

    public boolean deleteFriend(Long userId, Long friendId) {
        userDao.deleteFriend(userId, friendId);
        return true;
    }

    public void delete(Long id) {
        userDao.delete(id);
    }

    public List<User> getFriends(Long id) {
        return userDao.getFriends(id);
    }

    public List<User> getMutualFriends(Long id, Long otherId) {

        return userDao.getFriendsCommon(id, otherId);
    }

    private boolean userNotValid(User user) {
        if (!user.getBirthday().isBefore(LocalDate.now())) {
            log.debug("Пользователь не мог родиться в будущем ¯\\_(ツ)_/¯ ");
            return true;
        }
        return false;
    }

}