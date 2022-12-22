package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(Long userId);

    boolean checkExistenceById(Long userId);

    List<User> getAllUsers();

    User editUser(User user, Long userId);

    void deleteUser(Long userId);
}
