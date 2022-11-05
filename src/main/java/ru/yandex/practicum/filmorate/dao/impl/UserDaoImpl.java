package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
public class UserDaoImpl implements UserStorage {

    private JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public User create(User user) {
        String sql = "insert into users(login,name,email,birthday)" +
                "values(?,?,?,?)";
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Long id = jdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET user_id=?," +
                " name = ?," +
                "login = ?," +
                "email = ?," +
                "birthday = ?" +
                "WHERE user_id = ?";

        jdbcTemplate.update(sql, user.getId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ", id);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"), rs.getDate("birthday").toLocalDate()));
    }

    @Override
    public Optional<User> getById(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return Optional.ofNullable(jdbcTemplate.query(sql, rs -> rs.next() ? new User(rs.getLong("user_id"), rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"), rs.getDate("birthday").toLocalDate()) : null, id));
    }

    public boolean addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friends (first_user_id,second_user_id,status_id)" +
                "VALUES(?,?,?)";
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        user.getFriends().add(friendId);
        User friend = getById(friendId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (friend.getFriends().contains(userId)) {
            jdbcTemplate.update(sql, userId, friend, 2);
            return true;
        } else if (!friend.getFriends().contains(userId)) {
            jdbcTemplate.update(sql, userId, friendId, 1);
            return true;
        }
        return false;
    }

    public List<User> getFriends(Long id) {
        String sql = "SELECT * FROM users WHERE user_id IN(SELECT second_user_id FROM friends WHERE first_user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()), id);
    }

    public void deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE first_user_id =? AND second_user_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getFriendsCommon(Long id, Long otherId) {
        String sql =
                "SELECT u.USER_ID," +
                        "       u.email," +
                        "       u.login," +
                        "       u.name," +
                        "       u.birthday " +
                        "FROM friends f2 " +
                        "INNER JOIN users u ON u.user_id = f2.second_user_id " +
                        "WHERE f2.first_user_id = ?" +
                        " AND f2.second_user_id IN ( " +
                        "    SELECT f1.second_user_id " +
                        "    FROM friends f1 " +
                        "    WHERE f1.first_user_id = ?)";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()), id, otherId);
    }
   
}
