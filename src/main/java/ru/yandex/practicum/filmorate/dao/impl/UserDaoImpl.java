package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public class UserDaoImpl implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
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
        String sql = "update users set user_id=?," +
                " name = ?," +
                "login = ?," +
                "email = ?," +
                "birthday = ?" +
                "where user_id = ?";

        jdbcTemplate.update(sql, user.getId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("delete from users where id = ", id);
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"), rs.getDate("birthday").toLocalDate()));
    }


    @Override
    public User getUserById(Long id) {
        String sql = "select * from users where user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"), rs.getDate("birthday").toLocalDate()), id);
    }

    public boolean addFriend(Long userId, Long friendId) {
        String sql = "insert into friends (first_user_id,second_user_id,status_id)" +
                "values(?,?,?)";
        User user = getUserById(userId);
        user.getFriends().add(friendId);
        User friend = getUserById(friendId);
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
        String sql = "select * from users where user_id in(select second_user_id from friends where first_user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()), id);
    }

    public void deleteFriend(Long userId, Long friendId) {
        String sql = "delete from friends where first_user_id =? and second_user_id = ?";
        jdbcTemplate.update(sql,userId, friendId);
    }

    public List<User> getFriendsCommon(Long id, Long otherId) {
        String sql =
                "select u.USER_ID," +
                        "       u.email," +
                        "       u.login," +
                        "       u.user_name," +
                        "       u.birthday " +
                        "from friends f2 " +
                        "inner join users u on u.user_id = f2.second_user_id " +
                        "where f2.first_user_id = ?" +
                        " and f2.second_user_id in ( " +
                        "    select f1.second_user_id " +
                        "    from friends f1 " +
                        "    where f1.first_user_id = ?)";

        return jdbcTemplate.query(sql,(rs, rowNum) -> new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                rs.getDate("birthday").toLocalDate()), id, otherId);
    }

    public List<Long> userIds() {
        return jdbcTemplate.query("select user_id from users", (rs, rowNum) -> rs.getLong("user_id"));
    }

}
