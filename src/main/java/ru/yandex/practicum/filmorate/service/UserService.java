package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }
    public User createUser(User user){
       return userStorage.createUser(user);
    }
    public User updateUser(User user){
        return userStorage.updateUser(user);
    }
    public List<User>findAllUsers(){
        return userStorage.findAll();
    }
    public User getUserById(Long id){
       return userStorage.getUserById(id);
    }

    public boolean addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            return true;
        }
        return false;
    }

    public boolean deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user != null || friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            return true;
        }
        return false;
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getUserById(id);
        return user.getFriends().stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long id, Long otherId) {
        User user=userStorage.getUserById(id);
        User friend=userStorage.getUserById(otherId);
        return user.getFriends().stream().filter(friend.getFriends()::contains).map(userStorage::getUserById).
                collect(Collectors.toList());
    }

}
